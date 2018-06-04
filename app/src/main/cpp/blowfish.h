/* blowfish.h */
#ifndef BLOWFISH_H
#define BLOWFISH_H

//#define YH_DEBUG

#define UWORD32 unsigned int
#define UBYTE08 unsigned char

#define MAXKEYBYTES 56          /* 448 bits */

typedef struct
{
	UWORD32 S[4][256], P[18];
} blf_ctx,*LPBLF_CTX;

#if defined(__cplusplus)
extern "C" {
#endif
    
    UWORD32 BlowFish_F(blf_ctx *, UWORD32 x);
    void Blowfish_encipher(blf_ctx *, UWORD32 *xl, UWORD32 *xr);
    void Blowfish_decipher(blf_ctx *, UWORD32 *xl, UWORD32 *xr);

    void FillSubKey(blf_ctx * bc);
    void EncSubKey(blf_ctx * bc, unsigned char key[], int keybytes);

    short InitializeBlowfish(blf_ctx *, unsigned char key[], int keybytes);
    void blf_enc(blf_ctx *c, UWORD32 *data, int blocks);
    void blf_dec(blf_ctx *c, UWORD32 *data, int blocks);
    void blf_key(blf_ctx *c, unsigned char *key, int len);

    ////////////////////////
    //void BlowfishOFBNLF()
    //	根据输入的信息, 采用 Blowfish 的 OFBNLF 方式, 对输入的数据进行加密处理.
    //	add by richard youngh, 2007-5-29 14:14
    //	Input:
    //		isDec: 		是不是加密, 1 Dec, 0 : Enc 
    //		pCtx:  		blowfish context 
    //		key :  		key c-string 
    //		pPlainText:	clear text to encrypt 
    //      nLen:       pPlainText的字节长度
    //		pDst : 		加解密结果存放空间:
    //					对于 Encrypt , 由于一般要补0 , 它的大小只要满足比明文大一个block 即 strlen(pPlainText) + 8 
    //					对于 Decrypt , 不用补0 , 所以只要与转入的密文一样大就可以了. 即 strlen(pPlainText)
    //		nDstBufLen: 结果空间大小
    //	Return : 
    //		return 		:输出结果的长度, 字节单位,  -1 表示结果空间大小不够. (注意结果不会自动补尾0, 如果调用者需要应自己补)
    ////////////////////////
    int Blowfish_OFBNLF(const char isDec , blf_ctx *pCtx, const char * key, const char * pPlainText, int nLen, char * pDst, int nDstBufLen);
#if defined(__cplusplus)
}
#endif

#endif
