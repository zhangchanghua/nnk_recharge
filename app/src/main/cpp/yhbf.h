//#include "PiaType.h"
#ifndef yhbf_h
#define yhbf_h

#include "blowfish.h"

#define BB_Encode 1 //先用Blowfish加密再base64编码
#define BB_Decode 0 //先base64解码再用Blowfish解密

#if defined(__cplusplus)
extern "C" {
#endif
    
    //nTag=1/0: 先加密再base64编码/反向操作
	//成功返回pszValue中数据的长度,失败返回-1
    int BlowFishWithBase64(const  char* lpszBuf , int nTag, const  char* pszKey, char *pszValue, int nLen);
    
#if defined(__cplusplus)
}
#endif

#endif
