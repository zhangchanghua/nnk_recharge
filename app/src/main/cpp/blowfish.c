/* blowfish.c */
#include <netinet/in.h>
#include "blowfish.h"
#include "bf_tab.h"
#include <string.h>
#ifdef PIA_ANDROID
#include <netinet/in.h>
#endif
#if defined(PIA_WINDOWS) || defined(PIA_SIMU)
#include <winsock.h>
#endif
#ifdef PIA_WINPHONE
#include <winsock2.h>
#endif
#ifdef PIA_MTK
#include "soc_api.h"
#define  ntohl(A) soc_ntohl(A)
#define  htonl(A) soc_htonl(A)
#endif

#ifdef PIA_IOS
//#include <sys/_endian.h>   //del by fxm 20140220 此处头文件不要打开，否则会造成htonl函数得不到正确的结果
#include "stdlib.h"
#endif

#define N               16
#define noErr            0
#define DATAERROR         -1
#define KEYBYTES         8

UWORD32 BlowFish_F(blf_ctx *bc, UWORD32 x)
{
   UWORD32 a = 0;
   UWORD32 b = 0;
   UWORD32 c = 0;
   UWORD32 d = 0;
   UWORD32 y = 0;

   d = x & 0x00FF;
   x >>= 8;
   c = x & 0x00FF;
   x >>= 8;
   b = x & 0x00FF;
   x >>= 8;
   a = x & 0x00FF;
   y = bc->S[0][a] + bc->S[1][b];
   y = y ^ bc->S[2][c];
   y = y + bc->S[3][d];

   return y;
}

void Blowfish_encipher(blf_ctx *bc, UWORD32 *xl, UWORD32 *xr)
{
   UWORD32  Xl;
   UWORD32  Xr;
   UWORD32  temp;
   short          i;

   Xl = *xl;
   Xr = *xr;

   for (i = 0; i < N; ++i)
   {
      Xl = Xl ^ bc->P[i];
      Xr = BlowFish_F(bc, Xl) ^ Xr;

      temp = Xl;
      Xl = Xr;
      Xr = temp;
   }

   temp = Xl;
   Xl = Xr;
   Xr = temp;

   Xr = Xr ^ bc->P[N];
   Xl = Xl ^ bc->P[N + 1];

   *xl = Xl;
   *xr = Xr;
}

void Blowfish_decipher(blf_ctx *bc, UWORD32 *xl, UWORD32 *xr)
{
   UWORD32  Xl;
   UWORD32  Xr;
   UWORD32  temp;
   short          i;

   Xl = *xl;
   Xr = *xr;

   for (i = N + 1; i > 1; --i)
   {
      Xl = Xl ^ bc->P[i];
      Xr = BlowFish_F(bc, Xl) ^ Xr;

      /* Exchange Xl and Xr */
      temp = Xl;
      Xl = Xr;
      Xr = temp;
   }

   /* Exchange Xl and Xr */
   temp = Xl;
   Xl = Xr;
   Xr = temp;

   Xr = Xr ^ bc->P[1];
   Xl = Xl ^ bc->P[0];

   *xl = Xl;
   *xr = Xr;
}

void FillSubKey(blf_ctx * bc)
{
	short i ;
	
	for (i = 0; i < N+2; i++)							//把原始的bpf 拷贝到running subkey P 中  
	{
		bc->P[i] = bfp[i];
	}
	for (i = 0; i < 256; i++)							//把原始的ks? 拷贝到running subkey S 
	{
		bc->S[0][i] = ks0[i];
		bc->S[1][i] = ks1[i];
		bc->S[2][i] = ks2[i];
		bc->S[3][i] = ks3[i];
	}
}	

void EncSubKey(blf_ctx * bc, unsigned char key[], int keybytes)
{
	 short          i;
	 short          j;
	 short          k;
	 UWORD32  data;
	 UWORD32  datal;
	 UWORD32  datar;

	j = 0;												//用用户选择的 key 循环填充 running P 
	for (i = 0; i < N + 2; ++i)							
	{
		data = 0x00000000;
		for (k = 0; k < 4; ++k)
		{
			data = (data << 8) | key[j];
			j = j + 1;
			if (j >= keybytes)
			{
	  			j = 0;
			}
		}
		bc->P[i] = bc->P[i] ^ data;
	}

	datal = 0x00000000;									//初始用全0串, 循环加密P[i],P[i+1] , 生成新 P[i+2], P[i+3]
	datar = 0x00000000;

	for (i = 0; i < N + 2; i += 2)
	{
		Blowfish_encipher(bc, &datal, &datar);

		bc->P[i] = datal;
		bc->P[i + 1] = datar;
	}

	for (i = 0; i < 4; ++i)								//初始用P[16],P[17], 类似于上面方法循环加密4个 S数组
	{
		for (j = 0; j < 256; j += 2)
		{

			Blowfish_encipher(bc, &datal, &datar);

			bc->S[i][j] = datal;
			bc->S[i][j + 1] = datar;
		}
	}
}

short InitializeBlowfish(blf_ctx *bc, unsigned char key[], int keybytes)
{
	//我把原来这个函数分解成两个函数, 完全照搬过去的.
	//	第一个函数仅实现把固定的 P S 拷贝到 running subkey 中
	//	第二个函数实现根据输入的key , 对当前的 running subkey 用同样的算法再次加密.
	
	FillSubKey(bc);
	EncSubKey(bc, key, keybytes);
	return 0;
}


void blf_key (blf_ctx *c, unsigned char *k, int len)
{
	InitializeBlowfish(c, k, len);
}

void blf_enc(blf_ctx *c, UWORD32 *data, int blocks)
{
	UWORD32 *d;
	int i;

	d = data;
	for (i = 0; i < blocks; i++)
	{
		Blowfish_encipher(c, d, d+1);
		d += 2;
	}
}

void blf_dec(blf_ctx *c, UWORD32 *data, int blocks)
{
	UWORD32 *d;
	int i;

	d = data;
	for (i = 0; i < blocks; i++)
	{
		Blowfish_decipher(c, d, d+1);
		d += 2;
	}
}



int Blowfish_OFBNLF(const char isDec , blf_ctx *pCtx, const char * key, const char * pPlainText, int nLen , char * pDst, int nDstBufLen) 
{
	UWORD32 data[2], * pDstLong;
	int i ;
	int nCopyLen, nDstLen;
	
	pDstLong = (UWORD32 * ) pDst;
	blf_key(pCtx, (unsigned char *)key, (int)strlen(key));
	
	nDstLen = 0; 
	//把输入串格式化成 unsigned long * 
	//nLen = (int)strlen(pPlainText);
	for (i=0 ; i< nLen ; ) 
	{
		if (nLen -i > 8 ) 
			nCopyLen = 8; 
		else {
			nCopyLen = nLen-i; 
			memset(data, 0,(int)sizeof(data));	//尾部补0 
		}
		memcpy(&data , &pPlainText[i], nCopyLen);
		//把人类的big endian 换成local 
		data[0] = ntohl(data[0]);		
		data[1] = ntohl(data[1]);

		if (isDec) 
			blf_dec(pCtx, data, 1);
		else 
			blf_enc(pCtx, data, 1);
		*pDstLong = htonl(data[0]); 
		pDstLong ++ ;
		*pDstLong = htonl(data[1]); 
		pDstLong ++ ;
		nDstLen += 8; 	//增加一个block 
		if (nDstLen + 8 >= nDstBufLen ) 
			return -1;
		i += nCopyLen; 
		if (i >= nLen ) 
			break;
		EncSubKey(pCtx, (unsigned char *)key, (int)strlen(key));
	}
	return nDstLen;
}

