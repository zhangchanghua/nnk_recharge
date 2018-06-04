/*
2010-12-01 14:16:02 3
关于 DH Diffie-hellman  算法的实现. 
-------------------
C 语言函数库实现要求: 
1: 求ZZ
	要求实现2个函数
		1: DHGenKey (bit_width, p, g, my_private_buf , my_public_buf) 
			本函数根据输入的前3个参数, 生成一个随机的 my_private, 并计算一个 my_public = g ^ my_private mod p
			bit_width 目前取 1024 
			2个 Buf 要注意大小要足够. 用于存放返回的结果. 可以考虑增加Buf_len 以指定 buf 大小. 

		2: DHGenZZ  (bit_width, p, my_private, oth_public, ZZ_buf) 
			本函数根据输入的信息, 计算并返回 ZZ = oth_public ^ my_private mod p 
			注意求ZZ 之前要先检查输入的 oth_pubblic 是不是合法的. 即在 2..p-1 之间
			1个 Buf 要注意大小要足够. 用于存放返回的结果. 可以考虑增加Buf_len 以指定 buf 大小. 

2: 求 Key  
	要求实现一个函数 :
		1: DHZZ2KEK(ZZ, Oid, Counter, SI, kek_bits, KEK_Key_buf); 
			本函数根据输入的信息, 多次循环生成 KEK, 达到 kek_bits 的宽度. 并返回. 
			1个 Buf 要注意大小要足够. 用于存放返回的结果. 可以考虑增加Buf_len 以指定 buf 大小. 
	
	它的逻辑大约是: 
	while (generate_byte * 8 < kek_bits) {
		Key += md5(ZZ, Oid, Counter, partyAInfo, kek_bits); 
		Counter ++ ; 
	}


-------------------
MsgSrv 通讯协议:
	R: DHGenKey bit_width p g serialno
	A: DHGenKey my_private my_public

	R: DHGenZZ bit_width p my_private oth_public serialno
	A: DHGenZZ ZZ serialno

	R: DHZZ2KEK ZZ oid partyAInfo kek_bits serialno
	A: DHZZ2KEK KEY_key serialno 

-------------------
berbon 通讯协议: (在 Init 包中增加, 或者复用相应字段) 
	R: index_group , public_client_y
		index_group: 		是使用哪个组的 p q g . 
		public_client_y:	是手机端生成的公钥 
	A: SI, public_server_y, 
		public_server_y:	是服务器端生成的公钥
		SI			是服务器端返回的SI 值, 用于作 partyAInfo. 

---- 实际应用  --- 
1: 对方以:  ZZ = (对方公钥) ^ (自己的私钥) mod p  求出 ZZ

2: 再按下面的方法求出 Key 
	oid 由于我们目前使用是 blowfish , 所以 oid 固定为:  - blowfishCBC  = 1.3.6.1.4.1.3029.1.1.2
		编码 ASN.1 后就是 0x06 0A 2B 06 01 04 01 97 55 01 01 02
			(其中: 3029 = 0x0bd5 = 1011 1101 0101 = (1011 1) (101 0101) = 0x17 55 = 0x97 55 ) 
	counter 是一个int 型数, 从1 开始 . 注意 network byte order , 即内存中是 0x00 00 00 01 
	partyAInfo, 采用通讯中的由srv 返回的 SI. 
	kek_bits : 我们指定采用 blowfish 的key length 即: 128 bits 

3: Group 在服务器端可以采用配置文件. 即允许多种 group 的组合.
	在 client 端也可以采用配置文件, 或者干粹写死也行. 
	目前我们都使用2 --Diffie-Hellman Group 2 (1024 bits)



*/

#ifndef __DH_H___
#define __DH_H___
#include "jhrsa.h"
//所有输出都为原始数据
 

void GetResult(LPBIGINT pResult, LPRESULTBUF pOutput);

#if defined(__cplusplus)
extern "C" {
#endif

/*
本函数根据输入的前3个参数, 生成一个随机的 my_private, 并计算一个 my_public = g ^ my_private mod p
2 个 Buf 要注意大小要足够. 用于存放返回的结果. 
pBit_width取 1024 but not used for now,2 < pPrivateKey < (p-1)
*/
void PiaGenDHKey(int pBit_width, char* pP, int pPLen, char* pG, int pGLen, char* pPrivateKey, int* pPrivateKeyLen
			  , char* pPublicKey, int* pPublicKeyLen) ;

/*
本函数根据输入的信息, 计算并返回 ZZ = oth_public ^ my_private mod p 
pBit_width取 1024 but not used for now
注意求ZZ 之前要先检查输入的 oth_pubblic 是不是合法的. 即在 2..p-1 之间
1个 Buf 要注意大小要足够. 用于存放返回的结果.
return 0=suceed,-1=failed,-2=非法的oth_public
*/
int PiaGenDHZZ(int pBit_width, char* pP, int pPLen, char* pPrivateKey, int pPrivateKeyLen
			, char* pOtherPublicKey, int pOtherPublicKeyLen, char* ZZ_buf, int*pZZLen) ;//pBit_width not used for now,2 < oth_public < (p-1),

/*
本函数根据输入的信息, 多次循环生成 KEK, 达到 kek_bits 的宽度. 并返回. 
1个 Buf 要注意大小要足够. 用于存放返回的结果. 
*/
void PiaDHZZ2KEK(char* pZZ, int pZZLen, char* pOid, int pOidLenth, char* pSI, int pKek_bits, char* pKEK_Key_buf); 


#if defined(__cplusplus)
}
#endif

#endif
/*
Diffe-Hellman算法

该算法是第一个公钥算法，由美国的Diffe和Hellman于1976年提出。其安全性源于在有限域上计算离散对数比计算指数更困难，该算法主要用于密钥交换。协议如下：首先A与B协商一个大的素数n和g ，g是模n的本原元;A选取一个大的随机数x并且发送给B：X=gx mod n;B选取一个大的随机数y并且发送给A：Y=gy mod n;A计算k2=Yx mod n;B计算k2=Xy mod n，k1和k2都等于gxy mod n，偷听者即使知道n，g，X和Y，也无法计算出k，除非他们计算离散对数，因此k是A与B的秘密密钥。
Diffie-Hellman密钥交换算法及其优化

   首次发表的公开密钥算法出现在Diffie和Hellman的论文中，这篇影响深远的论文奠定了公开密钥密码编码学。由于该算法本身限于密钥交换的用途，被许多商用产品用作密钥交换技术，因此该算法通常称之为Diffie-Hellman密钥交换。这种密钥交换技术的目的在于使得两个用户安全地交换一个秘密密钥以便用于以后的报文加密。

   Diffie-Hellman密钥交换算法的有效性依赖于计算离散对数的难度。简言之，可以如下定义离散对数：首先定义一个素数p的原根，为其各次幂产生从1 到p-1的所有整数根，也就是说，如果a是素数p的一个原根，那么数值

                     a mod p, a2 mod p, ..., ap-1 mod p

是各不相同的整数，并且以某种排列方式组成了从1到p-1的所有整数。

   对于一个整数b和素数p的一个原根a，可以找到惟一的指数i，使得

                     b = ai mod p     其中0 ≤ i ≤ (p-1)

指数i称为b的以a为基数的模p的离散对数或者指数。该值被记为inda ,p(b)。

   基于此背景知识，可以定义Diffie-Hellman密钥交换算法。该算法描述如下：

   1、有两个全局公开的参数，一个素数q和一个整数α，α是q的一个原根。

   2、假设用户A和B希望交换一个密钥，用户A选择一个作为私有密钥的随机数XA<q，并计算公开密钥YA=αXA mod q。A对XA的值保密存放而使YA能被B公开获得。类似地，用户B选择一个私有的随机数XB<q，并计算公开密钥YB=αXB mod q。B对XB的值保密存放而使YB能被A公开获得。

   3、用户A产生共享秘密密钥的计算方式是K = (YB)XA mod q。同样，用户B产生共享秘密密钥的计算是K = (YA)XB mod q。这两个计算产生相同的结果：

                 K = (YB)XA mod q

                   = (αXB mod q)XA mod q

                   = (αXB)XA mod q                   （根据取模运算规则得到）

                   = αXBXA mod q

                   = (αXA)XB mod q

                   = (αXA mod q)XB mod q

                   = (YA)XB mod q

因此相当于双方已经交换了一个相同的秘密密钥。

   4、因为XA和XB是保密的，一个敌对方可以利用的参数只有q、α、YA和YB。因而敌对方被迫取离散对数来确定密钥。例如，要获取用户B的秘密密钥，敌对方必须先计算

                  XB = indα ,q(YB)

然后再使用用户B采用的同样方法计算其秘密密钥K。

   Diffie-Hellman密钥交换算法的安全性依赖于这样一个事实：虽然计算以一个素数为模的指数相对容易，但计算离散对数却很困难。对于大的素数，计算出离散对数几乎是不可能的。

   下面给出例子。密钥交换基于素数q = 97和97的一个原根α = 5。A和B分别选择私有密钥XA = 36和XB = 58。每人计算其公开密钥

                   YA = 5^36 mod 97 = 50

                   YB = 5^58 mod 97 = 44

在他们相互获取了公开密钥之后，各自通过计算得到双方共享的秘密密钥如下：

                    K = (YB)^XA mod 97 = 44^36 mod 97 = 75

                    K = (YA)^XB mod 97 = 50^58 mod 97 = 75

从|50，44|出发，攻击者要计算出75很不容易。

   下图给出了一个利用Diffie-Hellman计算的简单协议。


   










   假设用户A希望与用户B建立一个连接，并用一个共享的秘密密钥加密在该连接上传输的报文。用户A产生一个一次性的私有密钥XA，并计算出公开密钥YA并将其发送给用户B。用户B产生一个私有密钥XB，计算出公开密钥YB并将它发送给用户A作为响应。必要的公开数值q和α都需要提前知道。另一种方法是用户A选择q和α的值，并将这些数值包含在第一个报文中。

   下面再举一个使用Diffie-Hellman算法的例子。假设有一组用户（例如一个局域网上的所有用户），每个人都产生一个长期的私有密钥XA，并计算一个公开密钥YA。这些公开密钥数值，连同全局公开数值q和α都存储在某个中央目录中。在任何时刻，用户B都可以访问用户A 的公开数值，计算一个秘密密钥，并使用这个密钥发送一个加密报文给A。如果中央目录是可信任的，那么这种形式的通信就提供了保密性和一定程度的鉴别功能。因为只有A和B可以确定这个密钥，其它用户都无法解读报文（保密性）。接收方A知道只有用户B才能使用此密钥生成这个报文（鉴别）。

   Diffie-Hellman算法具有两个吸引力的特征：

   1. 仅当需要时才生成密钥，减小了将密钥存储很长一段时间而致使遭受攻击的机会。
   2. 除对全局参数的约定外，密钥交换不需要事先存在的基础结构。 

   然而，该技术也存在许多不足：

   1. 没有提供双方身份的任何信息。
   2. 它是计算密集性的，因此容易遭受阻塞性攻击，即对手请求大量的密钥。受攻击者花费了相对多的计算资源来求解无用的幂系数而不是在做真正的工作。
   3. 没办法防止重演攻击。
   4. 容易遭受中间人的攻击。第三方C在和A通信时扮演B；和B通信时扮演A。A和B都与C协商了一个密钥，然后C就可以监听和传递通信量。中间人的攻击按如下进行： 

         1. B在给A的报文中发送他的公开密钥。 
         2. C截获并解析该报文。C将B的公开密钥保存下来并给A发送报文，该报文具有B的用户ID但使用C的公开密钥YC，仍按照好像是来自B的样子被发送出去。A收到C的报文后，将YC和B的用户ID存储在一块。类似地，C使用YC向B发送好像来自A的报文。
         3. B基于私有密钥XB和YC计算秘密密钥K1。A基于私有密钥XA和YC计算秘密密钥K2。C使用私有密钥XC和YB计算K1，并使用XC和YA计算K2。
         4. 从现在开始，C就可以转发A发给B的报文或转发B发给A的报文，在途中根据需要修改它们的密文。使得A和B都不知道他们在和C共享通信。 

   Oakley算法是对Diffie-Hellman密钥交换算法的优化，它保留了后者的优点，同时克服了其弱点。

   Oakley算法具有五个重要特征：

   1. 它采用称为cookie程序的机制来对抗阻塞攻击。
   2. 它使得双方能够协商一个全局参数集合。
   3. 它使用了现时来保证抵抗重演攻击。
   4. 它能够交换Diffie-Hellman公开密钥。
   5. 它对Diffie-Hellman交换进行鉴别以对抗中间人的攻击。 

   Oakley可以使用三个不同的鉴别方法：

   1. 数字签名：通过签署一个相互可以获得的散列代码来对交换进行鉴别；每一方都使用自己的私钥对散列代码加密。散列代码是在一些重要参数上生成的，如用户ID和现时。
   2. 公开密钥加密：通过使用发送者的私钥对诸如ID和现时等参数进行加密来鉴别交换。
   3. 对称密钥加密：通过使用某种共享密钥对交换参数进行对称加密，实现交换的鉴别。 

 本原根模数n url:  http://www.worldlingo.com/ma/enwiki/zh_cn/Primitive_root_modulo_n

 ----------Diffie-Hellman.h---------

#define LFSR(n)    {if (n&1) n=((n^0x80000055)>>1)|0x80000000; else n>>=1;}
#define ROT(x, y)  (x=(x<<y)|(x>>(32-y)))
#define MAX_RANDOM_INTEGER 2147483648 //Should make these numbers massive to be more secure
#define MAX_PRIME_NUMBER   2147483648 //Bigger the number the slower the algorithm

class Diffie_Hellman{
public:
 Diffie_Hellman();
 int CreatePrimeAndGenerator();
 unsigned __int64 GetPrime();
 unsigned __int64 GetGenerator();
 unsigned __int64 GetPublicKey();
 void ShowValue(unsigned __int64 key);
 unsigned __int64 GetKey(unsigned __int64 HisPublieKey);
 int SetPrimeAndGenerator(unsigned __int64 Prime,unsigned __int64 Generator);
private:
 __int64 GetRTSC( void );
 unsigned __int64 GenerateRandomNumber(void);
 __int64 XpowYmodN(__int64 x, __int64 y, __int64 N);
 bool IsItPrime (__int64 n, __int64 a) ;
 bool MillerRabin (__int64 n, __int64 trials);
 unsigned __int64 GeneratePrime();
 int CreatePrivateKey();
 int CreatePublicKey();
 int GenerateKey(unsigned __int64 HisPublicKey);
 unsigned __int64 p; //素数
 unsigned __int64 g; //对应的本原根
 unsigned __int64 X; //私钥
 unsigned __int64 Y; //公钥
 unsigned __int64 Key;//通讯密钥
};

--------------Diffie-Hellman.cpp--------------

#include "Diffie-Hellman.h"
#include<iostream>
Diffie_Hellman::Diffie_Hellman(){
 p=0;
 g=0;
 X=0;
 Y=0;
 Key=0;
}

__int64 Diffie_Hellman::GetRTSC( void )
{
 int tmp1 = 0;
 int tmp2 = 0;
 
 __asm
 {
  RDTSC;   // Clock cycles since CPU started
  mov tmp1, eax;
  mov tmp2, edx;
 }

 return ((__int64)tmp1 * (__int64)tmp2);
}
unsigned __int64 Diffie_Hellman::GenerateRandomNumber(void)
{
  static unsigned long rnd = 0x41594c49;
  static unsigned long x   = 0x94c49514;

  LFSR(x);
  rnd^=GetRTSC()^x;
  ROT(rnd,7);

  return (unsigned __int64)GetRTSC() + rnd;
}

__int64 Diffie_Hellman::XpowYmodN(__int64 x, __int64 y, __int64 N)
{
 __int64 tmp = 0;
 if (y==1) return (x % N);

 if ((y&1)==0)
 {
  tmp = XpowYmodN(x,y/2,N);
  return ((tmp * tmp) % N);
 }
 else
 {
  tmp = XpowYmodN(x,(y-1)/2,N);
  tmp = ((tmp * tmp) % N);
  tmp = ((tmp * x) % N);
  return (tmp);
 }
}

bool Diffie_Hellman::IsItPrime (__int64 n, __int64 a)
{
 __int64 d = XpowYmodN(a, n-1, n);
 if (d==1)
  return true;
 else
  return false;
 
}

bool Diffie_Hellman::MillerRabin (__int64 n, __int64 trials)
{
 __int64 a = 0;

 for (__int64 i=0; i<trials; i++)
 {
  a = (rand() % (n-3))+2;// gets random value in [2..n-1]
  
  if (IsItPrime (n,a)==false)
  {
   return false;
   //n composite, return false
  }
 } return true; // n probably prime
}

unsigned __int64 Diffie_Hellman::GeneratePrime()
{
 unsigned __int64 tmp = 0;

 tmp =  GenerateRandomNumber() % MAX_PRIME_NUMBER;

 //ensure it is an odd number
 if ((tmp & 1)==0)
  tmp += 1;

 if (MillerRabin(tmp,5)==true) return tmp;
 
 do
 {
  tmp+=2; 
 } while (MillerRabin(tmp,5)==false);
  
 return tmp;
}
int Diffie_Hellman::CreatePrimeAndGenerator()// 产生素数p，和它的本原根g
{ 
 unsigned __int64 q;
 bool f=true;
 while(f){
  p=GeneratePrime();
  q=p*2+1;
  if(MillerRabin(q,5)==true)
   f=false;
 }
 f=true;
 while(f){
  g=GenerateRandomNumber() % (p-2);
  if(XpowYmodN(g, 2, p)!=1 && XpowYmodN(g, q, p)!=1)
   f=false;
 }
 return 0;
}

unsigned __int64 Diffie_Hellman::GetPrime(){
 return p;
}
unsigned __int64 Diffie_Hellman::GetGenerator(){
 return g;
}

int Diffie_Hellman::CreatePrivateKey(){
  X=GenerateRandomNumber() %(p-1);
 return 0;
}

int Diffie_Hellman::CreatePublicKey(){
 //先设置私钥
 if(X==0)
  CreatePrivateKey();
 Y=XpowYmodN(g, X, p);
 return 0;
}
unsigned __int64 Diffie_Hellman::GetPublicKey(){
 if(Y==0) CreatePublicKey();
 return Y;
}
void Diffie_Hellman::ShowValue(unsigned __int64 key){
 char s[20];
 _i64toa(key,s,10);
 std::cout<<s<<std::endl;
}

int Diffie_Hellman::GenerateKey(unsigned __int64 HisPublicKey){
 Key=XpowYmodN(HisPublicKey, X, p);
 return 0;
}

unsigned __int64 Diffie_Hellman::GetKey(unsigned __int64 HisPublicKey){
 if(Key==0)
  GenerateKey(HisPublicKey);
 return Key;
}

int Diffie_Hellman::SetPrimeAndGenerator(unsigned __int64 Prime,unsigned __int64 Generator){
 p=Prime;
 g=Generator;
 return 0;
}

----------------Main.cpp------------

#include<iostream>
#include "Diffie-Hellman.h"
using namespace std;

int main(){
 unsigned __int64 p=0,g=0,Alice_Y,Bob_Y,Alice_key,Bob_key;
 char prime[20],generator[20],sAlice_key[20],sBob_key[20];
 Diffie_Hellman Alice,Bob;
 Alice.CreatePrimeAndGenerator();
 p=Alice.GetPrime();
 g=Alice.GetGenerator();
 _i64toa(p,prime,10);
 _i64toa(g,generator,10);
 cout<<"prime:"<<prime<<endl<<"generator:"<<generator<<endl;
 Bob.SetPrimeAndGenerator(p,g);
 //p=Bob.GetPrime();
 //g=Bob.GetGenerator();
 //_i64toa(p,prime,10);
 //_i64toa(g,generator,10);
 //cout<<"prime:"<<prime<<endl<<"generator:"<<generator<<endl;
 Alice_Y=Alice.GetPublicKey();
 //_i64toa(Alice_Y,prime,10);cout<<prime<<endl;
 Bob_Y=Bob.GetPublicKey();
 //_i64toa(Bob_Y,prime,10);cout<<prime<<endl;
 Alice_key=Alice.GetKey(Bob_Y);
 //_i64toa(Alice_key,prime,10);cout<<prime<<endl;
 Bob_key=Bob.GetKey(Alice_Y);
 //_i64toa(Bob_key,prime,10);cout<<prime<<endl;
 _i64toa(Alice_key,sAlice_key,10);
 _i64toa(Bob_key,sBob_key,10);
 cout<<sAlice_key<<endl<<sBob_key<<endl;
 return 0;
}
*/

