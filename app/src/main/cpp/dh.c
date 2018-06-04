#if defined(PIA_WINDOWS) || defined(PIA_SIMU)
#include "windows.h"
#endif
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include "dh.h"
//#import <CommonCrypto/CommonDigest.h>
#include "md5.h"

void GetResult(LPBIGINT pResult,LPRESULTBUF pOutput)
{
	int len;
	char *p;
	if (pResult->Length > 1)
	{
		ReverseBigInt(pResult);//reverse data
	}
	p = (char*)pResult->Value;
	len = pResult->Length * sizeof(int);
	memcpy(pOutput->buf + pOutput->dataLen, p, len);
	pOutput->dataLen += len;
}

void PiaGenDHKey(int pBit_width, char* pP,int pPLen, char* pG,int pGLen, char* pPrivateKey,int* pPrivateKeyLen
			  , char* pPublicKey,int* pPublicKeyLen) //pBit_width not used for now,2 < pPrivateKey < (p-1)
{
	BIGINT bp,bg,privateKey,publicKey,min;
	RESULTBUF buf;
	InitExchangeBuffer(&bp);
	InitExchangeBuffer(&bg);
	InitExchangeBuffer(&privateKey);
	InitExchangeBuffer(&publicKey);
	InitExchangeBuffer(&min);
	Get(&bp,pP,pPLen,HEX);
	Get(&bg,pG,pGLen,HEX);
	MovInt64(&min,2);
	GetRandom(&bp,&min,&privateKey);
	ModExp(&bg,&privateKey,&bp,&publicKey);
	InitResultBuf(&buf,1024);
	GetResult(&publicKey,&buf);
	memcpy(pPublicKey,buf.buf,buf.dataLen);
	*pPublicKeyLen = buf.dataLen;

	ReleaseResultBuf(&buf);
	InitResultBuf(&buf,1024);
	GetResult(&privateKey,&buf);
	memcpy(pPrivateKey,buf.buf,buf.dataLen);
	*pPrivateKeyLen = buf.dataLen;
	//printf("the length of key is private: %d public: %d\n bits", *pPrivateKeyLen * 8, *pPublicKeyLen * 8);
	ReleaseResultBuf(&buf);
	ReleaseBigInt(&bp);
	ReleaseBigInt(&bg);
	ReleaseBigInt(&privateKey);
	ReleaseBigInt(&publicKey);
	ReleaseBigInt(&min);
}

int PiaGenDHZZ(int pBit_width, char* pP,int pPLen, char* pPrivateKey,int pPrivateKeyLen
			, char* pOtherPublicKey,int pOtherPublicKeyLen, char* ZZ_buf,int*pZZLen)//pBit_width not used for now,2 < oth_public < (p-1),return 0=suceed,-1=failed,-2=非法的oth_public
{
	int ret = 0;
	RESULTBUF buf;
	BIGINT bp,privateKey,publicKey,zz;//,min;
	InitExchangeBuffer(&bp);
	InitExchangeBuffer(&privateKey);
	InitExchangeBuffer(&publicKey);
	InitExchangeBuffer(&zz);
	
	GetRaw(EM_DECODE, &publicKey, pOtherPublicKey, pOtherPublicKeyLen);//使用EM_DECODE避免Add padding
	//MovInt64(&min,2);
	//if (Cmp(&min,&publicKey)==1 )
	//{
	//	return -1;
	//}
	Get(&bp, pP, pPLen, HEX);
	if (Cmp(&publicKey,&bp)==1 )
	{
		ret = -2;
	}
	else
	{
	GetRaw(EM_DECODE, &privateKey, pPrivateKey, pPrivateKeyLen);//使用EM_DECODE避免Add padding

	ModExp(&publicKey,&privateKey,&bp,&zz);
	InitResultBuf(&buf,1024);
	GetResult(&zz,&buf);
	memcpy(ZZ_buf,buf.buf,buf.dataLen);
	*pZZLen = buf.dataLen;
	ReleaseResultBuf(&buf);
	ret = 0;
	}
	ReleaseBigInt(&bp);
	ReleaseBigInt(&privateKey);
	ReleaseBigInt(&publicKey);
	ReleaseBigInt(&zz);
	return ret;
}



void PiaDHZZ2KEK(char* pZZ,int pZZLen, char* pOid,int pOidLenth, char* pSI, int pKek_bits, char* pKEK_Key_buf)
{
	int counter=1,currentCount=0,len,n,counterOffset,max = pKek_bits * 2 / 8;
	char buf[4096],counterBuf[4],c,keybitBuf[4];
	memcpy(keybitBuf,&pKek_bits,4);
	//改变整数在内存中的顺序
	c = keybitBuf[0];
	keybitBuf[0] = keybitBuf[3];
	keybitBuf[3] = c;
	c = keybitBuf[1];
	keybitBuf[1]=keybitBuf[2];
	keybitBuf[2]=c;
	len=0;
	memcpy(buf,pZZ,pZZLen);
	len+=pZZLen;
	memcpy(buf+len,pOid,pOidLenth);
	len+=pOidLenth;
	counterOffset = len;

	len=counterOffset+4;
	n=(int)strlen(pSI);
	memcpy(buf+len,pSI,n);
	len+=n;
	memcpy(buf+len,keybitBuf,4);
	len+=4;
	while (currentCount < max) 
	{
		//改变整数在内存中的顺序
		memcpy(counterBuf,&counter,4);
		c = counterBuf[0];
		counterBuf[0] = counterBuf[3];
		counterBuf[3] = c;
		c = counterBuf[1];
		counterBuf[1]=counterBuf[2];
		counterBuf[2]=c;

		memcpy(buf + counterOffset, counterBuf, 4);
		Pia_MD5(buf, len, pKEK_Key_buf + currentCount, 32);
		currentCount += 32;//MD5字符串长度为32
		++counter; 
	}
	pKEK_Key_buf[max]=0;
}

