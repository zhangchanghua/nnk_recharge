
#if defined(__cplusplus)
extern "C" {
#endif
    
	//
	void Pia_MD5(const char *pSrc, int pSrcLength, char *pszBuf ,int pBufLen);


    //摘要转16进制字符串
    void digestToString(unsigned char* pDigest,int pLen);

	char* MDString(const char *string);
    
#if defined(__cplusplus)
}
#endif
