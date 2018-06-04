/*
 * base64.h
 *
 * Header for base64 encoding and decoding algorithm. 
 *
 * Copyright (c) 2001--2002 by Wolfgang Wieser (wwieser@gmx.de)
 *
 * This file may be distributed and/or modified under the terms of the 
 * GNU General Public License version 2 as published by the Free Software 
 * Foundation. (See COPYING.GPL for details.)
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */


#ifndef _HLIB_Base64EncDec_H_
#define _HLIB_Base64EncDec_H_ 1
/*
//----------- Changed by Richard Youngh Begin 2007-5-29 12:42 ---------
//#include <hlib/prototypes.h>
#include <stdio.h>
#include "unistd.h"
#include <string.h>
#include <sys/types.h>
#include <stdlib.h>
#include <time.h>
//----------- Changed by Richard Youngh End  2007-5-29 12:42 ---------
#define ssize_t1 signed unsigned int
*/

//----------- Changed by Richard Youngh Begin 2007-5-29 12:42 ---------
//#include <hlib/prototypes.h>
#include <stdio.h>
#include <string.h>
//#include <sys/types.h>
#include <stdlib.h>
#include <time.h>

//#define ssize_t1 int
//#define u_int32_t unsigned int
#ifndef PIA_ANDROID
typedef long ssize_t1 ;
#endif
#ifndef PIA_ANDROID
#ifndef PIA_IOS
typedef unsigned int u_int32_t ;
#endif
#endif
#if defined(__cplusplus)
extern "C" {
#endif


/* ---ENCODE---
 * Reads in binary (8 bit) input data in _in of size inlen and encodes 
 * it as base64 storing the result in _out of size outlen. 
 * You may set lwidth to something non-zero to make Base64Encode() 
 * insert newlines every lwidth*4 chars. (lwidth=1 -> every 4 chars; 
 * lwidth=0 -> disable this feature)
 * If state is non-NULL state info is updated. 
 * State info (carry) is needed only if you have a large amount of 
 * 8bit data which cannot be encoded in one single call. 
 * To do this, set up the u_int32_t state value to zero and pass a 
 * pointer to it whenever calling Base64Encode(). The function will 
 * store non-encoded bytes (max. 2 bytes + 2 bit counter) in state 
 * to be able to process them the next time unless you call 
 * Base64Encode() with inlen=0 to tell it that this is the end of the 
 * input data. (state will be 0 after that but better relay on the 
 * return values for error diagnosis.) 
 * When reaching input data end (either state!=NULL and inlen=0 or 
 * state=NULL), the base64 termination char `=' is appended to the 
 * output. 
 * Note: the current byte quartett count is also stored in state as 
 *   14 bit value so that the base64 text stays formatted even if you 
 *   have to call Base64Encode() repeatedly. So, you don't try to 
 *   set lwidth to values >2^14 (that is 2^14*4=65536 chars per line). 
 * 
 * Return value: 
 *  >=0 -> number of bytes in output
 *   -4 -> output buffer too small 
 */
extern ssize_t1 Base64Encode(
	const char *_in,size_t inlen,
	char *_out,size_t outlen,
	u_int32_t *state/*=NULL*/,int lwidth/*=0*/);

/* ---DECODE---
 * Reads in the base64 encoded string passed in _in of size inlen and 
 * stores the decoded result in buffer _out of size outlen. 
 * If state is non-NULL state info is updated. 
 * State info (carry) is needed only if you have a large amount of 
 * base64 data which cannot be decoded in one single call. 
 * To do this, set up the u_int32_t state value to zero and pass a 
 * pointer to it whenever calling Base64Decode(). The function will 
 * store non-decoded chars (max. 18 bits + 2 bit counter) in state 
 * to be able to process them the next time unless 
 *  - the base64 termination char (`=') is encountered or
 *  - you call Base64Decode() with inlen=0 which does just the same 
 *    as if you merely pass "=" as input. 
 * (state will be 0 after reading `=' / when passig inlen=0 but 
 *  better relay on the return values for error diagnosis.)
 * Return value: 
 *  >=0 -> number of bytes in output
 *   -2 -> illegal char in input
 *   -3 -> premature end (only if state is NULL or base64 termination 
 *         char `=' is encountered too early)
 *   -4 -> output buffer too small 
 */
      
extern ssize_t1 Base64Decode(
	const char *_in,size_t inlen,
	char *_out,size_t outlen,
	u_int32_t *state /*=NULL*/);

//base64 编码导出API
//pSource-- 要编码的缓冲区指针
//pSourceLen--要编码的缓冲区数据长度
//pBuffer-- 存放结果的缓冲区，必须预先分配好足够的空间
//pBufferSize -- pBuffer的大小
//返回值：
// >0 -> 编码结果的长度
//   -4 -> 存放结果的缓冲区太小 
extern int PiaBase64Encode(const char* pSource, int pSourceLen, char* pBuffer, int pBufferSize);

//base64 解码导出API
//pSource-- 要解码的字符串
//pBuffer-- 存放结果的缓冲区，必须预先分配好足够的空间
//pBufferSize -- pBuffer的大小
//返回值：
// >0 -> 解码结果的长度
//   -2 -> 无效的pSource
//   -3 -> pSource数据不全
//   -4 -> 存放结果的缓冲区太小 
extern int PiaBase64Decode(const char* pSource, char* pBuffer, int pBufferSize);

#if defined(__cplusplus)
}
#endif

#endif   /* _HLIB_Base64EncDec_H_ */

