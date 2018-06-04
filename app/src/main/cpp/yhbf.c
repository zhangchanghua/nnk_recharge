#if defined(PIA_WINDOWS) || defined(PIA_SIMU)
#include "windows.h"
#endif
#include <stdio.h>
#include "base64.h"
#include "yhbf.h"
//#include "PiaInclude.h"



//#define MAX_RESULT 1024

int BlowFishWithBase64(const char* lpszBuf , int nTag, const char* pszKey, char *pszValue, int nLen)
{
	LPBLF_CTX lpBlf_ctx = NULL;
	int  nRstLen = 0;
	const char *pKey = pszKey;
	const char *pText = lpszBuf;	
	char  *pszBASE64Result = NULL;
	char  *pszBlowFishResult = NULL;
	int nBASE64BufSize,nBlowFishBufSize;
	int iRtn = -1;
	if (NULL == lpszBuf || NULL == pszValue || NULL == pszKey)
	{
		//PiaTrace((char*)"KNL2BlowFishWithBase64 lpszBuf or pszValue or pszKey == NULL");
		return iRtn;
	}
	
	lpBlf_ctx = (LPBLF_CTX)malloc((int)sizeof(blf_ctx));
	if (lpBlf_ctx == NULL)
	{
		//PiaTrace("KNL2BlowFishWithBase64 pszBASE64Result lpBlf_ctx == NULL");
		return iRtn;
	}
    //dbg("KNL1nTag=%d source=%s key=%s", nTag, lpszBuf ,  pszKey);
	memset(lpBlf_ctx, 0, (int)sizeof(blf_ctx));
	if (nTag == BB_Encode)
	{
		nBASE64BufSize = (int)strlen(lpszBuf)+2*8+1;
		nBlowFishBufSize = nBASE64BufSize*4/3+1;
	}
	else
	{
		nBASE64BufSize = strlen(lpszBuf)/4.0*3+10;
		nBlowFishBufSize = nBASE64BufSize+2*8+1;
	}
	pszBASE64Result = (char *)malloc(nBASE64BufSize);
	if (pszBASE64Result ==  NULL)
	{
		//PiaTrace("KNL2BlowFishWithBase64 pszBASE64Result ==  NULL");
		if (lpBlf_ctx != NULL)
		{
			free(lpBlf_ctx);
			lpBlf_ctx = NULL;
		}
		return iRtn;
	}
	memset(pszBASE64Result,0,nBASE64BufSize);
	pszBlowFishResult = (char *)malloc(nBlowFishBufSize);
	if (pszBlowFishResult ==  NULL)
	{
		if (pszBASE64Result !=  NULL)
		{
			free(pszBASE64Result);
			pszBASE64Result = NULL;
		}
		if (lpBlf_ctx != NULL)
		{
			free(lpBlf_ctx);
			lpBlf_ctx = NULL;
		}
		//PiaTrace("KNL2pszBlowFishResult ==  NULL");
		return iRtn;
	}
	memset(pszBlowFishResult, 0, nBlowFishBufSize);
	if (nTag == BB_Encode ) 
	{
		nRstLen = Blowfish_OFBNLF(0, lpBlf_ctx, pKey , pText, (int)strlen(pText) , pszBASE64Result, nBASE64BufSize);
		
		//dbg("KNL1 pText=%s pszBASE64Result=%s pKey=%s nRstLen=%d",pText,pszBASE64Result,pKey,nRstLen);		
		if (nRstLen < 0)
		{
			//PiaTrace("KNL2BlowFishWithBase64 nRstLen < 0");
		}
        else
        {
			pszBASE64Result[nRstLen] = '\0';
            nRstLen = (int)Base64Encode(pszBASE64Result, nRstLen, pszBlowFishResult, nBlowFishBufSize, NULL, 0);		
			if(nRstLen < 0)
			{
				//PiaTrace("KNL2BlowFishWithBase64 nRstLen < 0");
			}
			else
			{
				pszBlowFishResult[nRstLen] = '\0';
			}
            //dbg("KNL1 pszBlowFishResult=%s pszBASE64Result=%s  nRstLen=%d",pszBlowFishResult,pszBASE64Result,nRstLen);
        }
	}
	else if (nTag == BB_Decode ) 
	{
		nRstLen = (int)Base64Decode(pText, (int)strlen(pText), pszBASE64Result, nBASE64BufSize, NULL);
		//dbg("KNL1 pText = %s pKey = %s szBASE64Result = %s",pText,pKey,pszBASE64Result);
		if (nRstLen < 0)
		{
			//PiaTrace("KNL2BlowFishWithBase64 nRstLen < 0");
		}	
        else
        {
			pszBASE64Result[nRstLen] = '\0';
            nRstLen = Blowfish_OFBNLF(1, lpBlf_ctx, pKey , pszBASE64Result , nRstLen, pszBlowFishResult , nBlowFishBufSize );		
			if(nRstLen < 0)
			{
				//PiaTrace("KNL2BlowFishWithBase64 nRstLen < 0");
			}
			else
			{
				pszBlowFishResult[nRstLen] = '\0';
			}
            //dbg("KNL1 pszBlowFishResult=%s  nRstLen=%d", pszBlowFishResult, nRstLen);
        }
	}	
	if(nRstLen >= 0)
	{
		nRstLen = (int)strlen(pszBlowFishResult);   //此处应该测的是pszBlowFishResult长度而不是pszValue长度，pszValue长度不就是nLen的长度吗？   fxm 20120831
		if (nRstLen < nLen)
		{
			memset(pszValue, 0, nLen);
			strcpy(pszValue, pszBlowFishResult);	
			iRtn = nRstLen;
		}
		else
		{
			//PiaTrace("KNL2error:BlowFishWithBase64 pszValue size is small");
		}
	}
	if (pszBASE64Result !=  NULL)
	{  
		free(pszBASE64Result);
		pszBASE64Result = NULL;
	}
	if (pszBlowFishResult !=  NULL)
	{
		free(pszBlowFishResult);
		pszBlowFishResult = NULL;
	}
	if (lpBlf_ctx != NULL)
	{
		free(lpBlf_ctx);
		lpBlf_ctx = NULL;
	}
	return iRtn;
}

