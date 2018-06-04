#ifndef __JHRSA_H___
#define __JHRSA_H___


/*****************************************************************
大数运算库头文件：jhrsa.h
作者：fangle.liu@gmail.com
版本：1.2 (2003.5.13)
修改：aishang@126.com 09-23-2010
说明：适用于C，1024位RSA运算
*****************************************************************/
#include "piaType.h"
#define USE_PTR_VALUE 

#define BI_MAXLEN 256//128//1024// //从1024改为256可以提高速度20%
#define RU_LEN BI_MAXLEN / 4
#define DEC 10
#define HEX 16
#define RSAUNIT unsigned int
//#define RSAUNIT unsigned char
#define RSAUNIT_SIZE sizeof(RSAUNIT)
#define BIGINT_BUF_SIZE BI_MAXLEN/2*RSAUNIT_SIZE

//const static char* CharTable="0123456789abcdefghijklmnopqrstuvwxyz"; 
//const static char* CharTable="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 

#pragma pack(1)

typedef struct stBigInt
{
	short Size;//Value的总长度,单位为RSAUNIT
	//大数在0x100000000进制下的长度    
    short Length;
	//用数组记录大数在0x100000000进制下每一位的值
#ifdef USE_PTR_VALUE
	RSAUNIT* Value;
#else
	RSAUNIT Value[BI_MAXLEN/2];
#endif
}BIGINT,*LPBIGINT;
#define BIGINT_SIZE sizeof(BIGINT)

#ifdef USE_BIGINT_PTR
typedef struct stBigIntPtr
{
	//大数在0x100000000进制下的长度    
    int Length;
	//用数组记录大数在0x100000000进制下每一位的值
	RSAUNIT* Value;
}BIGINT_PTR,*LPBIGINT_PTR;
#define BIGINT_PTR_SIZE sizeof(BIGINT_PTR)
#endif

typedef struct stResultBuf
{
	int size;
	int dataLen;
#ifdef STATIC_BUF
	char buf[1024];
#else
	char* buf;
#endif
}RESULTBUF,*LPRESULTBUF;

#pragma pack()

typedef enum eEncryptMode
{
	EM_ENCODE,
	EM_DECODE,
}ENCRYPT_MODE;


typedef void (*LPRsaCallback)(ENCRYPT_MODE pMode,LPBIGINT pResult,LPRESULTBUF pOutput,int k);
typedef void (*LPRsaSetSource)(ENCRYPT_MODE pMode,LPBIGINT pResult,char* text,int textLen);

void InitBigInt(LPBIGINT p, int pSize);

void InitKey(LPBIGINT p);
void InitExchangeBuffer(LPBIGINT p);
void ReleaseBigInt(LPBIGINT p);
void InitResultBuf(LPRESULTBUF pResultBuf,int pSize);
void ReleaseResultBuf(LPRESULTBUF pResultBuf);
//清空数据
void Clear(LPBIGINT p);
void ClearBigIntBuf(LPBIGINT p);
//拷贝数据
void MovInt64(LPBIGINT p,unsigned long long A);
//拷贝数据
void Mov(LPBIGINT pTo,LPBIGINT pFrom);
//比较数据大小
int Cmp(LPBIGINT p,LPBIGINT A); 
//数据相加
void Add0(LPBIGINT p,LPBIGINT A);
//数据相加
//void Add(LPBIGINT p,LPBIGINT A,LPBIGINT pResult);
//数据相加
void AddLong(LPBIGINT p,unsigned int A,LPBIGINT pResult);
//数据相减
void Sub0(LPBIGINT pFrom,LPBIGINT pSub);
//数据相减
//void Sub(LPBIGINT p,LPBIGINT A,LPBIGINT pResult);
//数据相减
void SubLong(LPBIGINT p,unsigned int A,LPBIGINT pResult);
//数据相乘
void Mul(LPBIGINT p,LPBIGINT A,LPBIGINT pResult);
//数据相乘
void MulLong(LPBIGINT p,unsigned int A,LPBIGINT pResult);
//数据相除
void Div(LPBIGINT p,LPBIGINT A,LPBIGINT pResult);
void DivLong(LPBIGINT p,unsigned int A,LPBIGINT pResult);
//数据取模
void Mod(LPBIGINT p,LPBIGINT A,LPBIGINT pResul);
unsigned int ModLong(LPBIGINT p,unsigned int A); 
//数据开平方根
//void Sqrt(LPBIGINT p,LPBIGINT pResult);

/**//*****************************************************************
输入输出
Get，从字符串按10进制或16进制格式输入到大数
Put，将大数按10进制或16进制格式输出到字符串
*****************************************************************/
void Get(LPBIGINT p,char* pBuf,int pBufLen, unsigned int system/*=HEX*/);
//int Put(LPBIGINT p,char* pBuf,int pBufLen, unsigned long system/*=HEX*/);
void GetRaw(ENCRYPT_MODE pMode,LPBIGINT p,char* pBuf,int pBufLen);

/**//*****************************************************************
RSA相关运算
ModMul，布莱克雷算法求模乘
Euc，欧几里德算法求模逆
MonPro，蒙哥马利算法求模乘
ModExp，蒙哥马利算法求模幂
TestPrime，拉宾米勒算法进行素数测试
FindPrime，产生指定长度的随机大素数
*****************************************************************/
void ModMul(LPBIGINT p,LPBIGINT A, LPBIGINT B,LPBIGINT pResult);
void Euc(LPBIGINT p,LPBIGINT A,LPBIGINT pResult);
//void MonPro(LPBIGINT p,LPBIGINT A, LPBIGINT pMod, unsigned long n,LPBIGINT pResult,RSAUNIT* pMonProBuf);
void MonPro0(LPBIGINT p,LPBIGINT A, LPBIGINT pMod, unsigned int n,RSAUNIT* pMonProBuf);//结果直接存放到p中
void ModExp(LPBIGINT p,LPBIGINT pKey, LPBIGINT pMod,LPBIGINT pResult);
//void ModExp2(LPBIGINT N,LPBIGINT A,LPBIGINT B,LPBIGINT pResult);//计算 N^A mod B
void PutToRaw(ENCRYPT_MODE pMode,LPBIGINT pResult,LPRESULTBUF pOutput,int k);

void AddPadding(char* pData,int pDataLen,int k,int pPaddingMode, char** pResult,int* pResultLen);
void DePadding(char* pData,int pDataLen,int k,int* pResultLen);

//k为块大小
int GetLenAfterPadding(int pPaddingMode, int pDataLen, int k);

void InitPadding();
char GetPaddingChar(int pPaddingMode);


////加密解密测试性能方法
//void Test(char* pSource,int pSourceLen,char* pPublicKey,int pPublicKeyLen,
//		  char* pPrivateKey,int pPrivateKeyLen,char* pN,int pNLen,
//		  char* pEncrypted,int pEncryptedLen,char* pDecrypted,int pDecryptedLen,char* pMsg,int pMsgLen);

void RsaCall(ENCRYPT_MODE pMode,char* cipher,int cipherLen,LPBIGINT D,LPBIGINT N
			 ,LPRESULTBUF pOutput,LPRsaSetSource pRsaSetSource,LPRsaCallback pOnRsaEncoded);

void ReverseBigInt(LPBIGINT pData);
void ReverseBigIntByInt(LPBIGINT pData);

//数据加密解密,加密结果不转化为16进制存放到pResult.
//void RsaRaw(ENCRYPT_MODE pMode, char* text, int textLen, char* pKey, int pKeyLen, char* pMod, int pModLen, LPRESULTBUF pOutput);
//数据解密,加密结果不转化为16进制存放到pResult
void RsaDecode(char* text, int textLen, char* pKey, int pKeyLen, char* pMod, int pModLen, LPRESULTBUF pOutput);

//数据添加padding后加密,加密结果不转化为16进制存放到pResult.
void RsaEncodePadding(int pPaddingMode, char* text, int textLen, char* pKey, int pKeyLen, char* pMod, int pModLen, LPRESULTBUF pOutput);

//数据加密,加密结果不转化为16进制存放到pResult.text已经完成添加padding操作。
void RsaEncode(char* text, int textLen, char* pKey, int pKeyLen, char* pMod, int pModLen, LPRESULTBUF pOutput);

//产生随机数
void GetRandom(LPBIGINT pMax,LPBIGINT pMin,LPBIGINT pResult);

#ifdef USE_BIGINT_PTR
void InitBigIntPtr(LPBIGINT_PTR pBigIntPtr);
void ReleaseBigIntPtr(LPBIGINT_PTR pBigIntPtr);
void MovByPtr(LPBIGINT_PTR pTo,LPBIGINT_PTR pFrom);
void MovFromPtr(LPBIGINT pTo,LPBIGINT_PTR pFrom);
void MovToPtr(LPBIGINT_PTR pTo,LPBIGINT pFrom);
void CopyPtr(LPBIGINT_PTR pTo,LPBIGINT_PTR pFrom);
void EucByPtr(LPBIGINT p,LPBIGINT A,LPBIGINT pResult);
void MovInt64ToPtr(LPBIGINT_PTR p,unsigned long long A);
void ClearBigIntPtrBuf(LPBIGINT_PTR p);
//数据相除
void DivByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A,LPBIGINT_PTR pResult
			  ,LPBIGINT_PTR pTmp1,LPBIGINT_PTR pTmp2,LPBIGINT_PTR pTmp3,LPBIGINT_PTR pTmp4);
//数据相乘
void MulByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A,LPBIGINT_PTR pResult,LPBIGINT_PTR pTmp);

//数据取模
void ModByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A,LPBIGINT_PTR pResul
			  ,LPBIGINT_PTR pTmp1,LPBIGINT_PTR pTmp2,LPBIGINT_PTR pTmp3);
void SubByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A,LPBIGINT_PTR pResult,LPBIGINT_PTR pTmp);
//比较数据大小
int CmpByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A); 
//比较数据大小
int CmpToPtr(LPBIGINT_PTR p,LPBIGINT A); 
//数据相加
void AddByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A,LPBIGINT_PTR pResult,LPBIGINT_PTR pTmp);
//数据相乘
void MulLongByPtr(LPBIGINT_PTR p,unsigned int A,LPBIGINT_PTR pResult,LPBIGINT_PTR pTmp);

void DivLongByPtr(LPBIGINT_PTR p,unsigned int A,LPBIGINT_PTR pResult,LPBIGINT_PTR pTmp);

void ModMulByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A, LPBIGINT_PTR B,LPBIGINT_PTR pResult);


void ClearPtr(LPBIGINT_PTR p);
#endif 

#endif


/*
hua 说:
 加解密包的AppName = Crypt 
Request : RSA E mod public_key  text_encode clear_text
reply   : enc_text

Request : RSA D mod private_key text_encode enc_text 
reply   : clear_text 


Request : blowfish E cipher_mod text_encode key clear_text 
reply   : enc_text 

Request : blowfish D cipher_mod key text_encode enc_text 
reply   : clear_text 

 这是我现在考虑的大概的通讯msgsrv 协议。 
 前2个是rsa 的。 
 后面是对称算法的。 
 其中text_encode 可以是hex , base64 , raw 表示text 部分的编码方式
 你也可以再考虑一下， 看我是不是考虑的周全。 
 先这样。 88

*/

#if defined(__cplusplus)
extern "C" {
#endif

	/*RSA加密
	pSrc:要加密的数据
	pSrcLen:要加密的数据的字节长度
	pKey:加密密钥字符串
	pKeyLen:加密密钥的字节长度
	pMod:对方公钥
	pModLen:对方公钥的字节长度
	pResult:存放加密结果的指针，在函数内部分配，由调用者释放。
	pResult:结果数据的字节长度
	*/
	void PiaRsaEnc(char* pSrc, int pSrcLen, char* pKey, int pKeyLen, char* pMod, int pModLen, char** pResult, int* pResultLen);

	/*RSA解密
	pSrc:要解密的数据
	pSrcLen:要解密的数据的字节长度
	pKey:解密密钥字符串
	pKeyLen:解密密钥的字节长度
	pMod:对方公钥
	pModLen:对方公钥的字节长度
	pResult:存放解密结果的指针，在函数内部分配，由调用者释放。
	pResult:结果数据的字节长度
	*/
	void PiaRsaDec(char* pSrc, int pSrcLen, char* pKey, int pKeyLen, char* pMod, int pModLen, char** pResult, int* pResultLen);

	/*RSA加密后再进行Base64编码
	pSrc:要加密的数据
	pSrcLen:要加密的数据的字节长度
	pKey:加密密钥字符串
	pKeyLen:加密密钥的字节长度
	pMod:对方公钥
	pModLen:对方公钥的字节长度
	pResult:存放加密后再进行Base64编码的结果的指针，在函数内部分配，由调用者释放。
	pResult:结果数据的字节长度
	*/
	void PiaRsaBase64Enc(char* pSrc, int pSrcLen, char* pKey, int pKeyLen, char* pMod, int pModLen, char** pResult, int* pResultLen);

	/*Base64解码后再进行RSA解密
	pSrc:要解密的数据
	pSrcLen:要解密的数据的字节长度
	pKey:解密密钥字符串
	pKeyLen:解密密钥的字节长度
	pMod:对方公钥
	pModLen:对方公钥的字节长度
	pResult:存放Base64解码后再进行RSA解密的结果的指针，在函数内部分配，由调用者释放。
	pResult:结果数据的字节长度
	*/
	void PiaRsaBase64Dec(char* pSrc, int pSrcLen, char* pKey, int pKeyLen, char* pMod, int pModLen, char** pResult, int* pResultLen);
	/*
	功能：
	RSA加密后再进行Base64编码
	参数：
	pSrc:要加密的数据
	pSrcLen:要加密的数据的字节长度
	pModPubKey:指向存放berbon服务器传过来的公钥和模的缓存区的指针,服务器传过来的公钥和模已经base64编码了,函数内部会解码.
	iModPubKeyLen:公钥和模的的长度,以字节为单位
	pEncRst:存放加密后再进行Base64编码的结果的指针,函数内部分配,由调用者释放。
	pEncRstLen:存放结果数据的长度的指针
	返回值：
	无
	*/
	void BerRsaBase64Enc(char *pSrc, int iSrcLen, char* pModPubKey, int iModPubKeyLen, char** pEncRst, int* pEncRstLen);

	/*
	功能:将字符串表示的十六进制数转换到BYTE数组中
	返回值:buf的长度
	*/
	int StrToHex(const char *str, int len, BYTE* buf);

	//将buf中的数字转换为16进制字符串
	char* HexToStr(BYTE *buf,int nLen,char* str);

#if defined(__cplusplus)
}
#endif
