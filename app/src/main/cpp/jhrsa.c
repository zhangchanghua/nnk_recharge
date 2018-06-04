#if defined(PIA_WINDOWS) || defined(PIA_SIMU)
#include "windows.h"
#endif
#include "jhrsa.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#if !defined(PIA_MTK) && !defined(__WINDOWMOBILE__)
#endif
#include <time.h> // 计时
#include <math.h>

#include "base64.h"
#ifdef __WINDOWMOBILE__
	#include <altcecrt.h>
#endif

#ifndef PiaMalloc
#define PiaMalloc malloc
#define PiaFree free
#endif


//pSize为Value大小,单位为RSAUNIT
void InitBigInt(LPBIGINT p, int pSize)
{
	//ReleaseBigInt(p);
	p->Length = 0;
	p->Size = pSize;
#ifdef	USE_PTR_VALUE
	p->Value = (RSAUNIT*)PiaMalloc(pSize*RSAUNIT_SIZE);
#else
	memset(p->Value, 0, p->Size*RSAUNIT_SIZE);
#endif
}

void InitKey(LPBIGINT p)
{
	InitBigInt(p, BI_MAXLEN / 4 + 1);
}

void InitExchangeBuffer(LPBIGINT p)
{
	InitBigInt(p, BI_MAXLEN / 2);
}

void ReleaseBigInt(LPBIGINT p)
{
#ifdef	USE_PTR_VALUE
	if (p->Value)
	{
		PiaFree(p->Value);
		p->Value = NULL;
	}
	p->Size = p->Length = 0;
#endif
}

#define RSA_KEY_MAXLEN 2048

void InitResultBuf(LPRESULTBUF pResultBuf, int pSize)
{
	pResultBuf->size = pSize;
	pResultBuf->dataLen = 0;
#ifndef STATIC_BUF
	pResultBuf->buf = (char*)PiaMalloc(pSize);
#endif
	memset(pResultBuf->buf, 0, pSize);
}

void ReleaseResultBuf(LPRESULTBUF pResultBuf)
{
#ifndef STATIC_BUF
	if (pResultBuf->buf){
		PiaFree(pResultBuf->buf);
	}
#endif
}

void Clear(LPBIGINT p)
{
	p->Length = 1;
	memset(p->Value, 0, p->Size * RSAUNIT_SIZE);
}

void ClearBigIntBuf(LPBIGINT p)
{
	p->Length = 0;
	memset(p->Value, 0, p->Size * RSAUNIT_SIZE);
}

void MovInt64(LPBIGINT p,unsigned long long A)
{
	ClearBigIntBuf(p);
	if(A > 0xffffffff)
	{
		p->Length = 2;
		p->Value[1] = (RSAUNIT)(A >> 32);
	}
	else
	{
		p->Length = 1;
	}
	p->Value[0] = (RSAUNIT)A;
}

/**//****************************************************************************************
	大数赋值
	调用方式：N.Mov(A)
	返回值：无，N被赋值为A
	****************************************************************************************/
void Mov(LPBIGINT pTo,LPBIGINT pFrom)
{
	if (pTo != pFrom)
	{
		pTo->Length = pFrom->Length;
		memset(pTo->Value, 0, pTo->Size* RSAUNIT_SIZE);
		memcpy(pTo->Value, pFrom->Value, pFrom->Length * RSAUNIT_SIZE);
	}
}

//数据相加
void Add0(LPBIGINT p,LPBIGINT A)
{
	int i;
	RSAUNIT carry = 0;
	unsigned long long sum = 0;
	if(p->Length < A->Length)
	{
		p->Length = A->Length;
	}
	for(i = 0; i < p->Length; ++i)
	{
		sum = A->Value[i];
		sum += p->Value[i] + carry;
		p->Value[i] = (RSAUNIT)sum;
		carry = (RSAUNIT)(sum >> 32);
	}
	p->Value[p->Length] = carry;
	p->Length += carry;
}
/**//****************************************************************************************
	大数相加
	调用形式：N.Add(A)
	返回值：N+A
	****************************************************************************************/
//void Add(LPBIGINT p,LPBIGINT A,LPBIGINT pResult)
//{
//	int i;
//	RSAUNIT carry = 0;
//	unsigned long long sum = 0;
//	Mov(pResult, p);
//	if(pResult->Length < A->Length)
//	{
//		pResult->Length = A->Length;
//	}
//	for(i = 0; i < pResult->Length; ++i)
//	{
//		sum = A->Value[i];
//		sum += pResult->Value[i] + carry;
//		pResult->Value[i] = (RSAUNIT)sum;
//		carry = (RSAUNIT)(sum >> 32);
//	}
//	pResult->Value[addTmp.Length] = carry;
//	pResult->Length += carry;
//	return ;
//}
//数据相减
void Sub0(LPBIGINT pFrom,LPBIGINT pSub)
{
	RSAUNIT carry = 0;
	//unsigned long long num;
	int i;
	if(Cmp(pFrom,pSub) <= 0)
	{
		Clear(pFrom);
		return ;
	}
	//printf("SubPtr--p->Length=%d\r\n",p->Length);
	for (i = 0; i < pFrom->Length; ++i)
	{
		if((pFrom->Value[i] > pSub->Value[i]) || ((pFrom->Value[i] == pSub->Value[i]) && (carry == 0)))
		{
			pFrom->Value[i] = pFrom->Value[i] - carry - pSub->Value[i];
			carry = 0;
		}
		else
		{
			//num = 0x100000000 + pFrom->Value[i];
			pFrom->Value[i] = (RSAUNIT)(0x100000000 + pFrom->Value[i] - carry - pSub->Value[i]);
			carry = 1;
		}
	}
	while (pFrom->Length > 0 && pFrom->Value[pFrom->Length - 1] == 0)
	{
		--pFrom->Length;
	}
}
///**//****************************************************************************************
//	大数相减
//	调用形式：N.Sub0(A)
//	返回值：N-A
//	****************************************************************************************/
//void Sub(LPBIGINT p,LPBIGINT A,LPBIGINT pResult)
//{
//	BIGINT X;
//	RSAUNIT carry = 0;
//	unsigned long long num;
//	int i;
//	InitExchangeBuffer(&X);
//	if(Cmp(p,A) <= 0)
//	{
//		Clear(pResult);
//	}
//	else
//	{
//		Mov(&X, p);
//		//printf("SubPtr--p->Length=%d\r\n",p->Length);
//		for (i = 0; i < p->Length; ++i)
//		{
//			if((p->Value[i] > A->Value[i]) || ((p->Value[i] == A->Value[i]) && (carry == 0)))
//			{
//				X.Value[i] = p->Value[i] - carry - A->Value[i];
//				carry = 0;
//			}
//			else
//			{
//				num = 0x100000000 + p->Value[i];
//				X.Value[i] = (RSAUNIT)(num - carry - A->Value[i]);
//				carry = 1;
//			}
//		}
//		while(X.Value[X.Length - 1] == 0)
//		{
//			--X.Length;
//		}
//		Mov(pResult, &X);
//	}
//	ReleaseBigInt(&X);
//	return ;
//}

/**//****************************************************************************************
	大数相乘
	调用形式：N.Mul(A)
	返回值：N*A
	****************************************************************************************/
void Mul(LPBIGINT p, LPBIGINT A, LPBIGINT pResult)
{
	BIGINT mulTmp;
	unsigned long long sum,mul = 0;
	RSAUNIT carry = 0;
	int i,j;
	if (p->Length > RU_LEN 
		//|| A->Length > RU_LEN
		)
	{
		InitExchangeBuffer(&mulTmp);
	}
	else
	{
		InitKey(&mulTmp);
	}
	if(A->Length == 1)
	{
		//		printf("MulPtr--call MulLong\r\n");
		MulLong(p, A->Value[0], pResult);
	}
	else
	{
		ClearBigIntBuf(&mulTmp);
		mulTmp.Length = p->Length+A->Length-1;
		//	printf("A->Length=%d\r\n",A->Length);
		for (i = 0; i < mulTmp.Length; ++i)
		{
			sum = carry;
			carry = 0;
			for (j = 0; j < A->Length; ++j)
			{
				if(((i - j) >= 0) && ((i - j) < p->Length))
				{
					//		printf("i=%d j=%d i-j=%d %d\r\n",i,j,i-j,(i-j>=0));
					mul = p->Value[i-j];
					//		printf("mul=%u A->Value[j]=%u j=%u\r\n",mul,A->Value[j],j);
					mul *= A->Value[j];
					carry += mul>>32;
					mul = mul&0xffffffff;
					//		printf("sum==%u\r\n",sum);
					sum += mul;
				}
			}
			carry += sum >> 32;
			mulTmp.Value[i] = (RSAUNIT)sum;
		}
		if(carry)
		{
			++mulTmp.Length;
			mulTmp.Value[mulTmp.Length - 1] = (RSAUNIT)carry;
		}
		Mov(pResult, &mulTmp);
	}
	ReleaseBigInt(&mulTmp);

	return ;
}

void Div(LPBIGINT p,LPBIGINT A,LPBIGINT pResult)
{
	BIGINT X,Y,Z,P;
	int i,len;
	unsigned long long num,div;
	if (p->Length > RU_LEN
		|| A->Length > RU_LEN)
	{
		InitExchangeBuffer(&X);
		InitExchangeBuffer(&Y);
		InitExchangeBuffer(&Z);
		InitExchangeBuffer(&P);
	}
	else
	{
		InitKey(&X);
		InitKey(&Y);
		InitKey(&Z);
		InitKey(&P);
	}
	if (A->Length == 1)
	{
		DivLong(p, A->Value[0], pResult);
	}
	else
	{
		Mov(&Y, p);
		while(Cmp(&Y,A) >= 0)
		{       
			div = Y.Value[Y.Length - 1];
			num = A->Value[A->Length - 1];
			len = Y.Length - A->Length;
			if((div == num) && (len == 0))
			{
				MovInt64(&X, 1);
				break;
			}
			if((div <= num) && len)
			{
				--len;
				div=(div << 32) + Y.Value[Y.Length - 2];
			}
			div /= (num + 1);
			MovInt64(&Z, div);
			if(len)
			{
				Z.Length += len;
				for (i = Z.Length - 1; i >= len; --i)
				{
					Z.Value[i] = Z.Value[i-len];
				}
				memset(Z.Value,0,len * RSAUNIT_SIZE);
			}
			Add0(&X, &Z);//, &X);
			Mul(A, &Z, &P);
			Sub0(&Y, &P);//, &Y);
		}
		Mov(pResult, &X);
	}
	ReleaseBigInt(&X);
	ReleaseBigInt(&Y);
	ReleaseBigInt(&Z);
	ReleaseBigInt(&P);

	return ;
}
void Mod(LPBIGINT p,LPBIGINT A,LPBIGINT pResult)
{
	BIGINT X,Y;
	unsigned long long div,num;
	//RSAUNIT carry = 0;
	int i,len,n;
	if (p->Length > RU_LEN
		|| A->Length > RU_LEN)
	{
		InitExchangeBuffer(&X);
		InitKey(&Y);
		//InitExchangeBuffer(&Y);
	}
	else
	{
		InitKey(&X);
		InitKey(&Y);
	}
	n=Cmp(p, A);
	if(n < 0)
	{
		Mov(pResult, p);//A);
	}
	else if(n == 0)
	{
		Clear(pResult);
	}
	else
	{
		Mov(&X, p);
		while(1)
		{
			div = X.Value[X.Length - 1];
			num = A->Value[A->Length - 1];
			len = X.Length-A->Length;
			if((div <= num) && len)
			{
				--len;
				div = (div << 32) + X.Value[X.Length - 2];
			}
			div /= (num + 1);
			MovInt64(&Y, div);
			Mul(A, &Y, &Y);
			if(len)
			{
				Y.Length += len;
				//printf("Mod--Y.Length=%d len=%d\r\n",Y.Length,len);
				for(i = Y.Length - 1; i >= len; --i)
				{
					Y.Value[i] = Y.Value[i - len];
				}
				memset(Y.Value, 0, len * RSAUNIT_SIZE);
			}
			Sub0(&X, &Y);//, &X);
			n = Cmp(&X, A);
			if(n == 0)
			{
				Clear(pResult);
				ReleaseBigInt(&X);
				ReleaseBigInt(&Y);

				return ;
			}
			if(n < 0)
			{
				break;
			}
		}
		Mov(pResult, &X);
	}
	ReleaseBigInt(&X);

	ReleaseBigInt(&Y);

	return ;
}
//void Sqrt(LPBIGINT p,LPBIGINT pResult)
//{
//    BIGINT X,M,N;
//    unsigned long m,n;
//    n=p->Value[p->Length-1];
//    n=(RSAUNIT)sqrt((double)n);
//    m=n+1;
//    if(p->Length==1)
//	{
//		MovInt64(pResult,n);
//		return ;
//	}
//	Clear(&M);
//	Clear(&N);
//    N.Length=p->Length/2;
//    M.Length=N.Length;
//    if(p->Length&1)
//    {
//        M.Length++;
//        N.Length++;
//        M.Value[M.Length-1]=m;
//        N.Value[N.Length-1]=n;
//    }
//    else 
//    {
//        M.Value[M.Length-1]=(m<<16);
//        N.Value[N.Length-1]=(n<<16);
//    }
//    Add(&M,&N,&X);
//    DivLong(&X,2,&X);
//    while(1)
//    {
//		Mul(&X,&X,&X);
//        if(Cmp(p,&X)<0)
//		{
//			Mov(&M,&X);
//		}
//        else
//		{
//			Mov(&N,&X);
//		}
//		Sub0(&M,&N,&X);
//        if((X.Value[0]==1)&&(X.Length==1))
//		{
//			Mov(pResult,&N);
//			return ;
//		}
//        Add(&M,&N,&X);
//        DivLong(&X,2,&X);
//    }
//}
void AddLong(LPBIGINT p,unsigned int A,LPBIGINT pResult)
{
	BIGINT X;
	int i;
	unsigned long long sum;
	if (p->Length > RU_LEN)
	{
		InitExchangeBuffer(&X);
	}
	else
	{
		InitKey(&X);
	}
	Mov(&X, p);
	sum = X.Value[0] + A;
	X.Value[0] = (RSAUNIT)sum;
	if(sum > 0xffffffff)
	{
		i=1;
		printf("AddLong-X.Value[i]=%d\r\n", X.Value[i]);
		while(X.Value[i] == 0xffffffff)
		{
			X.Value[i] = 0;
			++i;
		}
		++X.Value[i];
		if(X.Length == i)
		{
			++X.Length;
		}
	}
	Mov(pResult, &X);
	ReleaseBigInt(&X);
	return ;
}

void SubLong(LPBIGINT p,unsigned int A,LPBIGINT pResult)
{
	BIGINT X;
	//unsigned long long num;
	int i=1;
	if (p->Length > RU_LEN)
	{
		InitExchangeBuffer(&X);
	}
	else
	{
		InitKey(&X);
	}
	Mov(&X, p);
	if(X.Value[0] >= A)
	{
		X.Value[0] -= A;
		Mov(pResult, &X);
	}
	else if(X.Length == 1)
	{
		Clear(pResult);
	}
	else
	{
		//num = 0x100000000 + X.Value[0];
		//X.Value[0] = (RSAUNIT)(num - A);
		X.Value[0] = (RSAUNIT)(0x100000000 + X.Value[0] - A);
		while(X.Value[i] == 0)
		{
			X.Value[i] = 0xffffffff;
			++i;
		}
		--X.Value[i];
		if(X.Value[i] == 0)
		{
			--X.Length;
		}
		Mov(pResult, &X);
	}
	ReleaseBigInt(&X);

	return ;
}
void MulLong(LPBIGINT p,unsigned int A,LPBIGINT pResult)
{
	BIGINT X;
	unsigned long long mul;
	RSAUNIT carry = 0;
	int i;
	if (p->Length > RU_LEN)
	{
		InitExchangeBuffer(&X);
	}
	else
	{
		InitKey(&X);
	}
	Mov(&X, p);
	//	if (p->Length==17)
	//printf("A=%llu ", A);
	for(i = 0; i < p->Length; ++i)
	{
		mul = p->Value[i];
		//if (p->Length==17)
		//{
		//	printf("p->Value[%d]=%u ", i, mul);
		//}
		mul = mul * A + carry;
		X.Value[i] = (RSAUNIT)mul;
		carry = (RSAUNIT)(mul >> 32);
		//if (p->Length==17)
		//{
		//	printf("p->Value[%d]=%u carry=%u\r\n", i, X.Value[i], carry);
		//}
	}
	//	if (p->Length==17)
	//printf("carry=%d\r\n",carry);
	if(carry)
	{
		++X.Length;
		X.Value[X.Length-1] = carry;
	}
	Mov(pResult,&X);
	ReleaseBigInt(&X);
	return ;
}
void DivLong(LPBIGINT p,unsigned int A,LPBIGINT pResult)
{
	BIGINT X;
	unsigned long long div,mul;
	RSAUNIT carry=0;
	int i;
	if (p->Length > RU_LEN)
	{
		InitExchangeBuffer(&X);
	}
	else
	{
		InitKey(&X);
	}
	Mov(&X,p);
	if(X.Length == 1)
	{
		X.Value[0] /= A;
		Mov(pResult,&X);
	}
	else
	{
		for(i = X.Length - 1; i >= 0; --i)
		{
			div = carry;
			div = (div << 32) + X.Value[i];
			X.Value[i] = (RSAUNIT)(div / A);
			mul = (div / A) * A;
			carry = (RSAUNIT)(div - mul);
		}
		if (X.Length > 0 && X.Value[X.Length - 1] == 0)
		{
			--X.Length;
		}
		Mov(pResult,&X);
	}	
	ReleaseBigInt(&X);

	return ;
}

unsigned int ModLong(LPBIGINT p,unsigned int A)
{
	int i;
	RSAUNIT carry=0;
	unsigned long long div;
	if(p->Length == 1)
	{
		return (p->Value[0] % A);
	}
	for(i = p->Length - 1; i >= 0; --i)
	{
		div = p->Value[i];
		div += carry * 0x100000000;
		carry = (RSAUNIT)(div % A);
	}
	return carry;
} 
/**//****************************************************************************************
	大数比较
	调用方式：N.Cmp(A)
	返回值：若N<A返回-1；若N=A返回0；若N>A返回1
	****************************************************************************************/
int Cmp(LPBIGINT p,LPBIGINT A)
{
	int i;
	if(p->Length > A->Length)
	{
		return 1;
	}
	if(p->Length < A->Length)
	{
		return -1;
	}
	for(i = p->Length - 1; i >= 0; --i)//右边是高位
	{
		if(p->Value[i] > A->Value[i])
		{
			return 1;
		}
		if(p->Value[i] < A->Value[i])
		{
			return -1;
		}
	}
	return 0;
} 

/**//*****************************************************************
	输入输出
	Get，从字符串按10进制或16进制格式输入到大数
	Put，将大数按10进制或16进制格式输出到字符串
	*****************************************************************/
void Get(LPBIGINT p,char* pBuf,int pBufLen, unsigned int system/*=HEX*/)
{
	int len=pBufLen,k,i;
	Clear(p);
	for( i=0;i<len;i++)
	{
		//printf("Get-i=%d ", i);
		MulLong(p,system,p);
		if((pBuf[i] >= '0') && (pBuf[i] <= '9'))
		{
			k = pBuf[i] - 48;
		}
		else if((pBuf[i] >= 'A') && (pBuf[i] <= 'Z'))
		{
			k = pBuf[i] - 55;
		}
		else if((pBuf[i] >= 'a') && (pBuf[i] <= 'z'))
		{
			k = pBuf[i] - 87;
		}
		else
		{
			k = 0;
		}
		//printf(("Get--k=%d p->length=%d\r\n"), k, p->Length);
		AddLong(p, k, p);
	}
}
/**//****************************************************************************************
	将大数按2进制到36进制格式输出为字符串
	调用格式：N.Put(str,sys)
	返回值：数据长度，参数str被赋值为N的sys进制字符串
	****************************************************************************************/
//int Put(LPBIGINT p,char* pBuf,int pBufLen, unsigned long system/*=HEX*/)
//{
//	int pos;
//	BIGINT X;
//	if((p->Length==1)&&(p->Value[0]==0))
//	{
//		pBuf[0]='0';
//		return 0;
//	}
//	Mov(&X,p);
//	pos = pBufLen-1;
//	while(X.Value[X.Length-1]>0)
//	{
//		pBuf[pos--]=CharTable[ModLong(&X,system)];
//		DivLong(&X,system,&X);
//	}
//	memmove(pBuf,pBuf+pos+1,pBufLen-pos-1);
//	pBuf[pBufLen-pos-1]=0;
//	return pBufLen-pos-1;
//}
//
/**//*****************************************************************
	RSA相关运算
	ModMul，布莱克雷算法求模乘
	Euc，欧几里德算法求模逆
	MonPro，蒙哥马利算法求模乘
	ModExp，蒙哥马利算法求模幂
	TestPrime，拉宾米勒算法进行素数测试
	FindPrime，产生指定长度的随机大素数
	*****************************************************************/
void ModMul(LPBIGINT p,LPBIGINT A, LPBIGINT B,LPBIGINT pResult)
{
	int i,j;
	BIGINT X,P;
	//printf("ModMul\r\n");	
	InitExchangeBuffer(&X);
	//InitKey(&X);
	//InitExchangeBuffer(&P);
	InitKey(&P);
	MulLong(A, p->Value[p->Length-1], &X);
	Mod(&X, B, &X);
	for(i = p->Length - 2; i >= 0; --i)
	{          
		for(j = X.Length;j > 0; --j)
		{
			X.Value[j] = X.Value[j - 1];
		}
		X.Value[0] = 0;
		++X.Length;
		//printf("ModMul for\r\n");
		MulLong(A, p->Value[i], &P);
		Add0(&X, &P);//, &X);
		Mod(&X, B, &X);
	}
	Mov(pResult, &X);
	ReleaseBigInt(&X);

	ReleaseBigInt(&P);
	return ;
}

/**//****************************************************************************************
	求模逆，即解同余方程NX%A=1，亦即解不定方程NX-AY=1的最小整数解
	调用方式：N.Euc(A)
	返回值：X,满足：NX%A=1
	****************************************************************************************/
void Euc(LPBIGINT p,LPBIGINT A,LPBIGINT pResult)
{
	BIGINT M,E,X,Y,I,J;
	int x,y;
	if (p->Length > RU_LEN
		|| A->Length > RU_LEN)
	{
		InitExchangeBuffer(&M);
		InitExchangeBuffer(&E);
		InitExchangeBuffer(&X);
		InitExchangeBuffer(&Y);
		InitExchangeBuffer(&I);
		InitExchangeBuffer(&J);
	}
	else
	{
		InitKey(&M);
		InitKey(&E);
		InitKey(&X);
		InitKey(&Y);
		InitKey(&I);
		InitKey(&J);
	}
	Mov(&M, A);
	Mov(&E, p);
	MovInt64(&X,0);
	MovInt64(&Y,1);
	x = y = 1;
	//printf("Euc start\r\n");
	while((E.Length != 1) || (E.Value[0] != 0))
	{
		//printf("Euc--Set I=Div(M,E)\r\n");
		Div(&M, &E, &I);
		//printf("Set J\r\n");
		Mod(&M, &E, &J);
		//printf("Set M\r\n");
		Mov(&M, &E);
		//printf("Set E\r\n");
		Mov(&E, &J);
		//printf(" Mov Y to J\r\n");
		Mov(&J, &Y);
		//printf("Mov to Y\r\n");
		Mul(&Y, &I, &Y);
		//printf("check x==y:%d\r\n",x==y);
		if(x == y)
		{ 
			if(Cmp(&X, &Y) >= 0)
			{
				Sub0(&X, &Y);//, &X);
				Mov(&Y,&X);
			}
			else
			{
				Sub0(&Y, &X);//, &Y);
				y = 0;
			}
		}
		else
		{
			//Add(&X, &Y, &Y);
			Add0(&Y, &X);//, &Y);
			x = 1 - x;
			y = 1 - y;
		}
		Mov(&X, &J);
	}
	if(x == 0)
	{
		Mov(&X, A);
		Sub0(&X, &J);//, &X);
	}
	Mov(pResult, &X);
	ReleaseBigInt(&M);

	ReleaseBigInt(&E);

	ReleaseBigInt(&X);

	ReleaseBigInt(&Y);

	ReleaseBigInt(&I);

	ReleaseBigInt(&J);
	return ;
}

void MonPro0(LPBIGINT p,LPBIGINT A, LPBIGINT pMod, unsigned int n,RSAUNIT* pMonProBuf)//结果直接存放到p中
{
	int i,j,k;
	unsigned long long m,sum;
	RSAUNIT carry = 0;
	memset(pMonProBuf, 0, BIGINT_BUF_SIZE + BIGINT_BUF_SIZE); 
	k = pMod->Length;
	for (i = 0; i < k; ++i)
	{
		carry = 0;
		for (j = 0; j < k; ++j)
		{
			sum = A->Value[i];
			sum = sum * p->Value[j] + pMonProBuf[i + j] + carry;
			pMonProBuf[i + j] = (RSAUNIT)sum;
			carry = (RSAUNIT)(sum >> 32);
		}
		pMonProBuf[i + k] = carry;
	}
	for(i = 0; i < k; ++i)
	{
		carry = 0;
		m = pMonProBuf[i] * n;   //modify by fxm 20150305 将n变量类型由unsigned long改成unsigned int,long在64位机器上占8个字节，在32位机器上占4个字节，因此当两个大数相乘，最后因long的变化，导致在64位上的结果比32位大。
		for(j = 0; j < k; ++j)
		{
			sum = m * pMod->Value[j] + pMonProBuf[i + j] + carry;
			pMonProBuf[i + j] = (RSAUNIT)sum;
			carry = (RSAUNIT)(sum >> 32);
		}
		for(j = i + k; j < k * 2; j++)
		{
			sum = pMonProBuf[j];
			sum = sum + carry;
			pMonProBuf[j] = (RSAUNIT)sum;
			carry = (RSAUNIT)(sum >> 32);
			if(carry == 0)
			{
				break;
			}
		}
	}
	pMonProBuf[k + k] = carry;
	p->Length = k + 1;
	memcpy(p->Value, pMonProBuf + k, p->Length * RSAUNIT_SIZE+RSAUNIT_SIZE);
	while (p->Length > 0 && p->Value[p->Length - 1] == 0)
	{
		--p->Length;
	}
	if (Cmp(p, pMod) >= 0)
	{
		//printf("ModPro\r\n");
		Sub0(p, pMod);
	}
}

/**//****************************************************************************************
	求蒙哥马利模乘
	调用方式：MonPro(p,A,pMod,n)，（2**(k-1)<pMod<2**k，R=2**k，R*R'%pMod=1，n*pMod[0]%0x100000000=-1） 
	返回值：X=p*A*R'%pMod
	****************************************************************************************/
//void MonPro(LPBIGINT p,LPBIGINT A, LPBIGINT pMod, unsigned long n,LPBIGINT pResult,RSAUNIT* pMonProBuf)
//{
//	BIGINT monProTmp;
//	int i,j,k;
//	RSAUNIT carry = 0;
//	unsigned long long m,sum;
//	InitExchangeBuffer(&monProTmp);
//	k = pMod->Length;
//	memset(pMonProBuf, 0, BIGINT_BUF_SIZE + BIGINT_BUF_SIZE); 
//	for (i = 0; i < k; ++i)
//	{
//		carry = 0;
//		for (j = 0; j < k; ++j)
//		{
//			//sum = A->Value[i];
//			sum = A->Value[i] * p->Value[j] + pMonProBuf[i + j] + carry;
//			pMonProBuf[i + j] = (RSAUNIT)sum;
//			carry = (RSAUNIT)(sum >> 32);
//		}
//		pMonProBuf[i + k] = carry;
//	}
//	for(i = 0; i < k; ++i)
//	{
//		carry = 0;
//		m = pMonProBuf[i] * n;
//		for(j = 0; j < k; ++j)
//		{
//			sum = m * pMod->Value[j] + pMonProBuf[i + j] + carry;
//			pMonProBuf[i + j] = (RSAUNIT)sum;
//			carry = (RSAUNIT)(sum >> 32);
//		}
//		for(j = i + k; j < k * 2; j++)
//		{
//			sum = pMonProBuf[j];
//			sum += carry;
//			pMonProBuf[j] = (RSAUNIT)sum;
//			carry = (RSAUNIT)(sum >> 32);
//			if(carry == 0)
//			{
//				break;
//			}
//		}
//	}
//	pMonProBuf[k + k] = carry;
//	monProTmp.Length = k + 1;
//	memcpy(monProTmp.Value, pMonProBuf + k, monProTmp.Length * RSAUNIT_SIZE+RSAUNIT_SIZE);
//	while(monProTmp.Value[monProTmp.Length - 1] == 0)
//	{
//		--monProTmp.Length;
//	}
//	if(Cmp(&monProTmp, pMod)>=0)
//	{
//		//printf("ModPro\r\n");
//		Sub0(&monProTmp, pMod);//, &monProTmp);
//	}
//	Mov(pResult, &monProTmp);
//	ReleaseBigInt(&monProTmp);
//	return ;
//}

/**//****************************************************************************************
	求模幂
	调用方式：ModExp(&N,&A,&B,&X)
	返回值：X=N^A%B
	****************************************************************************************/
void ModExp(LPBIGINT p,LPBIGINT pKey, LPBIGINT pMod,LPBIGINT pResult)
{
	BIGINT X,Y;
	int i,k;
	unsigned int n;
	RSAUNIT* MonProBuf;
	//long t;
#ifdef USE_BIGINT_PTR
	BIGINT_PTR tmp1,tmp2,tmp3,tmp4;
	InitBigIntPtr(&tmp1);
	InitBigIntPtr(&tmp2);
	InitBigIntPtr(&tmp3);
	InitBigIntPtr(&tmp4);
#endif
	InitExchangeBuffer(&X);
	//InitKey(&X);//cannot use
	//InitExchangeBuffer(&Y);
	InitKey(&Y);
	//t = GetTimeStart();
	k = pKey->Length * 32 - 32;
	n = pKey->Value[pKey->Length - 1];
	//printf("ModExp--start\r\n");
	while(n)
	{
		n = n >> 1;
		++k;
	}
	//printf("ModExp--Clear Y\r\n");
	//Clear(&X);
	ClearBigIntBuf(&Y);
	Y.Length = 2;
	Y.Value[1] = 1;
	MovInt64(&X, pMod->Value[0]);
	//t = GetTimeStart();
	//printf("ModExp--Call Euc\r\n");
#ifdef USE_BIGINT_PTR
	EucByPtr(&X, &Y, &X);
#else
	Euc(&X, &Y, &X);
#endif
	//printf("ModExp--Euc consumed %lu ms\r\n", GetTimeGap(t));
	//Sub0(&Y, &X, &X);
	//n = X.Value[0];
	//ClearBigIntBuf(&Y);
	//Y.Length = pMod->Length + 1;
	//Y.Value[Y.Length-1] = 1;
	////printf("ModExp\r\n");
	//Sub0(&Y, pMod, &X);
	Sub0(&Y, &X);//, &Y);
	n = Y.Value[0];
	ClearBigIntBuf(&X);
	X.Length = pMod->Length + 1;
	X.Value[X.Length - 1] = 1;
	//printf("ModExp\r\n");
	Sub0(&X, pMod);//, &X);

	//t = GetTimeStart();
#ifdef USE_BIGINT_PTR
	MovToPtr(&tmp1,p);
	MovToPtr(&tmp2,&X);
	MovToPtr(&tmp3,pMod);
	ModMulByPtr(&tmp1, &tmp2, &tmp3, &tmp4);
	MovFromPtr(&Y,&tmp4);
#else
	ModMul(p, &X, pMod, &Y);
#endif
	//printf("ModExp--ModMul consumed %lu ms\r\n",GetTimeGap(t));
	//printf("ModExp--cycle k=%d\r\n",k);
	//t = GetTimeStart();
	MonProBuf = (RSAUNIT*)PiaMalloc(BIGINT_BUF_SIZE + BIGINT_BUF_SIZE);
	for(i = k - 1; i >= 0; --i)
	{
		MonPro0(&X, &X, pMod, n,  MonProBuf);
		if((pKey->Value[i >> 5] >> (i & 31)) & 1)
		{
			MonPro0(&X, &Y, pMod, n,  MonProBuf);
		}
	}
	MovInt64(&Y, 1);
	MonPro0(&X, &Y, pMod, n, MonProBuf);
	Mov(pResult,&X);
	//printf("ModExp--MonPro consumed %lu ms\r\n",GetTimeGap(t));
	PiaFree(MonProBuf);
#ifdef USE_BIGINT_PTR
	ReleaseBigIntPtr(&tmp1);
	ReleaseBigIntPtr(&tmp2);
	ReleaseBigIntPtr(&tmp3);
	ReleaseBigIntPtr(&tmp4);
#endif
	//printf("ModExp--consumed %lu ms\r\n",GetTimeGap(t));
	ReleaseBigInt(&X);
	ReleaseBigInt(&Y);
	return ;
}
//void ModExp2(LPBIGINT N,LPBIGINT A,LPBIGINT B,LPBIGINT pResult)//计算 N^A mod B
//{
//	BIGINT X,Y,Z;
//	int i,j,k;
//	unsigned n;
//	unsigned long num;
//	k=A->Length*32-32;
//	num=A->Value[A->Length-1];
//	while(num)
//	{
//		num=num>>1;
//		k++;
//	}
//	Mov(&X,N);
//	for(i=k-2;i>=0;i--)
//	{
//		MulLong(&X,X.Value[X.Length-1],&Y);
//		Mod(&Y,B,&Y);
//		for(n=1;n<X.Length;n++)
//		{         
//			for(j=Y.Length;j>0;j--)
//			{
//				Y.Value[j]=Y.Value[j-1];
//			}
//			Y.Value[0]=0;
//			Y.Length++;
//			MulLong(&X,X.Value[X.Length-n-1],&Z);
//			Add(&Y,&Z,&Y);
//			Mod(&Y,B,&Y);
//		}
//		Mov(&X,&Y);
//		if((A->Value[i>>5]>>(i&31))&1)
//		{
//			MulLong(N,X.Value[X.Length-1],&Y);
//			Mod(&Y,B,&Y);
//			for(n=1;n<X.Length;n++)
//			{         
//				for(j=Y.Length;j>0;j--)
//				{
//					Y.Value[j]=Y.Value[j-1];
//				}
//				Y.Value[0]=0;
//				Y.Length++;
//				MulLong(N,X.Value[X.Length-n-1],&Z);
//				Add(&Y,&Z,&Y);
//				Mod(&Y,B,&Y);
//			}
//			Mov(&X,&Y);
//		}
//	}
//	Mov(pResult,&X);
//}
////数据加密解密,加密结果不转化为16进制存放到pResult
//void RsaRaw(ENCRYPT_MODE pMode,char* text,int textLen,char* pKey,int pKeyLen,char* pMod,int pModLen,LPRESULTBUF pOutput)
//{
//	BIGINT d,n;
//	InitKey(&d);     //add by fxm 20141130 需要初始化，否则会空指针挂掉
//	InitKey(&n);
//	Get(&d,pKey,pKeyLen,HEX);
//	Get(&n,pMod,pModLen,HEX);
//	RsaCall(pMode,text,textLen,&d,&n,pOutput,GetRaw,PutToRaw);
//}
//数据解密,加密结果不转化为16进制存放到pResult
void RsaDecode(char* text,int textLen,char* pKey,int pKeyLen,char* pMod,int pModLen,LPRESULTBUF pOutput)
{
	BIGINT d,n;
	InitKey(&d);
	InitKey(&n);

	Get(&d,pKey,pKeyLen,HEX);
	Get(&n,pMod,pModLen,HEX);
	RsaCall(EM_DECODE,text,textLen,&d,&n,pOutput,GetRaw,PutToRaw);
	ReleaseBigInt(&d);
	ReleaseBigInt(&n);
}

//k为块大小
int GetLenAfterPadding(int pPaddingMode, int pDataLen, int k)
{
	int cnt = pDataLen / (k - 8);
	if (cnt * (k-8) < pDataLen)
	{
		++cnt;
	}
	return cnt * k;
}


//数据加密,加密结果不转化为16进制存放到pResult.text已经完成添加padding操作。
void RsaEncode(char* text, int textLen, char* pKey, int pKeyLen, char* pMod, int pModLen, LPRESULTBUF pOutput)
{
	BIGINT d,n;
	InitKey(&d);
	InitKey(&n);
	Get(&d,pKey,pKeyLen,HEX);
	Get(&n,pMod,pModLen,HEX);
	RsaCall(EM_ENCODE,text,textLen,&d,&n,pOutput,GetRaw,PutToRaw);
	ReleaseBigInt(&d);
	ReleaseBigInt(&n);
}

//数据添加padding后加密,加密结果不转化为16进制存放到pResult.
void RsaEncodePadding(int pPaddingMode,char* text,int textLen,char* pKey,int pKeyLen,char* pMod,int pModLen,LPRESULTBUF pOutput)
{
	int len;
	char* rsc = NULL;
	if (pPaddingMode>0)
	{
		AddPadding(text,textLen,(int)strlen(pMod)/2,pPaddingMode, &rsc, &len);
	}
	else
	{
		AddPadding(text,textLen,(int)strlen(pMod)/2,0, &rsc, &len);
	}
	RsaEncode(rsc,len,pKey, pKeyLen, pMod,pModLen, pOutput);
	if (rsc)
	{
		PiaFree(rsc);
	}
}

void GetRaw(ENCRYPT_MODE pMode,LPBIGINT p,char* pBuf,int pBufLen)
{
	memcpy(p->Value, pBuf, pBufLen);
	p->Length = pBufLen / 4;
	if (p->Length * 4 < pBufLen)
	{
		++p->Length;
	}
	if (p->Length > 1)
	{
		ReverseBigInt(p);
	}
}
void PutToRaw(ENCRYPT_MODE pMode, LPBIGINT pResult,LPRESULTBUF pOutput,int k)
{
	int len;
	char *p;
	ReverseBigInt(pResult);//reverse data
	p = (char*)pResult->Value;
	if (EM_DECODE == pMode)//is decode
	{
		DePadding(p, pResult->Length * sizeof(int), k, &len);
	}
	else
	{
		len = pResult->Length * sizeof(int);
	}
	memcpy(pOutput->buf + pOutput->dataLen, p, len);
	pOutput->dataLen += len;
}

//void Test(char* pSource,int pSourceLen,char* pPublicKey,int pPublicKeyLen,
//		  char* pPrivateKey,int pPrivateKeyLen,char* pN,int pNLen,
//		  char* pEncrypted,int pEncryptedLen ,char* pDecrypted,int pDecryptedLen,char* pMsg,int pMsgLen)
//{
//	struct timeb tic,tic1;
//	struct timeb toc,toc1;
//	double t,t1;
//	BIGINT d,n,p;
//	//printf("jhrsa:Test start\r\n");
//	Get(&d,pPublicKey,pPublicKeyLen,HEX);
//	//printf("get public key\r\n");
//	Get(&n,pN,pNLen,HEX);
//	//printf("get n\r\n");
//	Get(&p,pPrivateKey,pPrivateKeyLen,HEX);
//	//printf("get private key\r\n");
//	//CBigInt c,d,n,p,r2;
//	//c.InPutFromStr(m_strSourceString);
//	//d.InPutFromStr(m_strPublicKey);
//	//n.InPutFromStr(m_strR);
//	printf("\r\n源数据:%s",pSource);
//	//printf("\r\nget current time\r\n");
//	ftime(&tic);
//	//printf("EncryptInner start\r\n");
//	RsaCall(pSource,pSourceLen,&d,&n,pEncrypted,pEncryptedLen);
//	//CBigInt r = c.ModExp(d,n);
//	//printf("get end time\r\n");
//	ftime(&toc);
//	t = (toc.time-tic.time)+((double)(toc.millitm-tic.millitm)/1000.0);
//	//r.Put(m_strEncryptString);
//	ftime(&tic1);
//	//CBigInt r2 = r.ModExp(p,n);
//	DecryptInner(pEncrypted,(int)strlen(pEncrypted),&p,&n,pDecrypted,pDecryptedLen);
//	ftime(&toc1);
//	t1 = (toc1.time-tic1.time)+((double)(toc1.millitm-tic1.millitm)/1000.0);
//	//r2.Put(m_strDecryptString);
//#ifdef WIN32
//	sprintf_s(pMsg,pMsgLen,"加密花费时间：%lf 解密花费时间：%lf\n",  t,t1);
//#else
//	sprintf(pMsg,"加密花费时间：%lf 解密花费时间：%lf\n",  t,t1);
//#endif
//	printf("\r\n加密结果:");
//	printf(pEncrypted);
//	printf("\r\n解密结果:");
//	printf(pDecrypted);
//	printf("\r\n测试结果:");
//	printf(pMsg);
//}
//char padding0[8],padding1[8];
//void InitPadding()
//{
//	memset(padding0, 0, 8);
//	memset(padding1, 0, 8);
//	padding1[1] = (char)1;
//	memset(padding1 + 2, 0xff, 5);
//}

void AddPadding(char* pData,int pDataLen,int k,int pPaddingMode, char** pResult,int* pResultLen)
{
	char *buf;	
	char* p = pData;
	char* p2;	
	int left,i,j,cnt = pDataLen / (k - 8),bkMode;
	if (pPaddingMode == 0)
	{
		bkMode = 0;
	}
	else
	{
		if (pPaddingMode != 1)//目前支持只pkcs1.5
		{
			return;
		}
		bkMode = rand() % 3;
#ifdef DEBUG
		//bkMode = 0;
#endif
	}
	if (cnt * (k - 8) < pDataLen)
	{
		++cnt;
	}
	*pResultLen = cnt * k;
	buf = (char*)PiaMalloc(cnt * k);
	memset(buf, 0, cnt * k);
	p2 = buf;
	left = pDataLen;
	for (i = 0; i < cnt; ++i)
	{
		if (left >= k - 8)
		{
			if (bkMode == 0)
			{
				memset(p2, 0, 8);
			}
			else if (bkMode == 1)
			{
				memset(p2, 0, 8);
				p2[1] = (char)1;
				memset(p2 + 2, 0xff, 5);
			}
			else 
			{
				p2[0] = 0;
				p2[1] = bkMode;
				for (j = 0; j < 5; ++j)
				{
					p2[j + 2] = GetPaddingChar(bkMode);
				}
			}
			memcpy(p2 + 8, p, k - 8);
			p2 += k;
			p += k - 8;
			left -= k - 8;
		}
		else
		{
			p2[0] = 0;
			p2[1] = bkMode;
			for (j = 0; j < k - 3 - left; ++j)
			{
				p2[j + 2] = GetPaddingChar(bkMode);
			}
			p2[j+2] = 0;
			memcpy(p2 + j + 2 + 1, p, left);
		}
	}
	*pResult = buf;
}

char GetPaddingChar(int pBkMode)
{
	if (pBkMode < 2)
	{
		return (char)(0 - pBkMode);
	}
	else
	{
		char c = (char)rand();
		if (c == 0)
		{
			c = 1;
		}
		return c;
	}
}

void DePadding(char* pData,int pDataLen,int k,int* pResultLen)
{
	char *p = pData;
	char *p2;
	int i,start,paddingMode=-1,end=0,j,resultLen=0,cnt;
	if (*pData!=0)
	{
		*pResultLen = pDataLen;
		return;
	}
	cnt = pDataLen / k;
	if (cnt * k < pDataLen)
	{
		++cnt;
	}
	p2 = pData;
	for (j=0;j<cnt;++j)
	{
		start=0;
		i=0;
		while (i<k)
		{
			if (*p==0)//found a zero
			{
				if (start)//already found the first zero
				{
					if (paddingMode>=0)//already found the mode 
					{
						if (paddingMode==0)//mode 0 it mean the next chars are zero until meet an non-zero
						{						
							//do nothing
						}
						else//meet the last zero in padding
						{
							end=1;
							//do nonthing
						}
					}
					else
					{
						paddingMode=0;
					}
				}
				else
				{
					start=1;
					paddingMode=-1;
					end=0;
				}
			}
			else//not zero
			{
				if (paddingMode<0 && (*p < 0 || *p > 2) )
				{
					paddingMode=0;
				}
				if (paddingMode>=0)//already found the mode 
				{
					if (paddingMode==0)//it means data
					{
						resultLen += k - i;
						memcpy(p2,p,k-i);
						p2+=k-i;
						p+=k-i;
						break;
					}
					else//padding mode 1 or 2
					{
						if (end==1)//already padding end, it means data
						{
							resultLen += k - i;
							memcpy(p2,p,k-i);
							p2+=k-i;
							p+=k-i;
							start=0;
							break;
						}
						else
						{
							//do nothing
						}
					}
				}
				else//not get padding mode yet
				{
					if (end == 0)
					{
						paddingMode=*p;//set the padding mode
					}
				}
			}
			++p;
			++i;
		}
	}
	pData[resultLen]=0;
	while (*(pData+resultLen-1)==0)
	{
		--resultLen;
	}
	*pResultLen = resultLen;
}


void RsaCall(ENCRYPT_MODE pMode,char* cipher,int cipherLen,LPBIGINT D,LPBIGINT N
			 ,LPRESULTBUF pOutput,LPRsaSetSource pRsaSetSource,LPRsaCallback pOnRsaEncoded)
{
	char *pSource;
	int count,i,len;
	BIGINT t;
	InitExchangeBuffer(&t);
	//InitKey(&t);//cannot use
	len = (N->Length*4);
	if (cipherLen <= len)
	{
		pRsaSetSource(pMode,&t,cipher,cipherLen);
		ModExp(&t,D,N,&t);
		pOnRsaEncoded(pMode,&t,pOutput,N->Length*sizeof(int));//模的数据块数
	}
	else
	{
		count = cipherLen / len ;
		if (count * len  < cipherLen)
		{
			++count;
		}
		pSource = cipher;
		for (i=0;i<count;++i)
		{
			if (len + i * len > cipherLen)
			{
				len = cipherLen - i * len;
			}
			pRsaSetSource(pMode,&t,pSource,len);
			ModExp(&t,D,N,&t);
			pOnRsaEncoded(pMode,&t,pOutput,N->Length*sizeof(int));
			pSource += len;
		}
	}
	ReleaseBigInt(&t);
}

void ReverseBigInt(LPBIGINT pData)
{
	int len=pData->Length * sizeof(int);
	int i;
	char c;
	char *p = (char*)pData->Value;
	int cnt = len/2;
	--len;		
	for (i=0;i<cnt;++i)
	{
		c=p[i];
		p[i] = p[len-i];
		p[len-i]=c;
	}	
}
void ReverseBigIntByInt(LPBIGINT pData)
{
	int len=pData->Length;
	int i;
	int c;
	int *p = (int*)pData->Value;
	int cnt=len/2;
	--len;		
	for (i=0;i<cnt;++i)
	{
		c=p[i];
		p[i] = p[len-i];
		p[len-i]=c;
	}	
}

int randInited = 0;
//产生随机数
void GetRandom(LPBIGINT pMax,LPBIGINT pMin,LPBIGINT pResult)
{
	int RANGE_MIN = pMin->Length;
	int RANGE_MAX = pMax->Length;
	int pos,count,i,val,max;

	Clear(pResult);
	if (0==randInited)
	{
		randInited=1;
#ifdef __WINDOWMOBILE__
		srand(_time64(0));	
#else
		srand((int)time(NULL));
#endif
	}
	if (RANGE_MAX == 1)
	{	
		max=pMax->Value[0]+2;
		while (1)
		{
			val = (rand()%max  + 2);
			MovInt64(pResult,val);
			if (pResult->Value[0] < pMax->Value[0])
			{
				break;
			}
		}
	}
	else
	{
		count = (int)(((double)rand() / (double)RAND_MAX) * RANGE_MAX + RANGE_MIN);
		//printf("count=%d\r\n",count);
		pResult->Length=0;
		for (i=0;i<count;++i)
		{
			pos = (int)(((double)rand() / (double)RAND_MAX) * RANGE_MAX);
			val = rand()*rand();			
			//printf("pos=%d val=%d\r\n",pos,val);
			pResult->Value[pos] = val;
			if (pos>pResult->Length)
			{
				pResult->Length=pos;
			}
		}
		++pResult->Length;
	}
}
#ifdef USE_BIGINT_PTR
void InitBigIntPtr(LPBIGINT_PTR pBigIntPtr)
{
	pBigIntPtr->Value = PiaMalloc(BIGINT_BUF_SIZE);
}
void ReleaseBigIntPtr(LPBIGINT_PTR pBigIntPtr)
{
	PiaFree(pBigIntPtr->Value);
}
void MovByPtr(LPBIGINT_PTR pTo,LPBIGINT_PTR pFrom)
{
	RSAUNIT* p = pTo->Value;
	pTo->Value = pFrom->Value;
	pFrom->Value = p;

	pTo->Length = pFrom->Length;
}
void MovFromPtr(LPBIGINT pTo,LPBIGINT_PTR pFrom)
{
	pTo->Length = pFrom->Length;
	memcpy(pTo->Value,pFrom->Value,BIGINT_BUF_SIZE);
}
void MovToPtr(LPBIGINT_PTR pTo,LPBIGINT pFrom)
{
	pTo->Length = pFrom->Length;
	memcpy(pTo->Value,pFrom->Value,BIGINT_BUF_SIZE);
}
void CopyPtr(LPBIGINT_PTR pTo,LPBIGINT_PTR pFrom)
{
	pTo->Length = pFrom->Length;
	memcpy(pTo->Value,pFrom->Value,BIGINT_BUF_SIZE);
}
void ClearBigIntPtrBuf(LPBIGINT_PTR p)
{
	memset(p->Value, 0, BIGINT_BUF_SIZE);
}
void MovInt64ToPtr(LPBIGINT_PTR p,unsigned long long A)
{
	ClearBigIntPtrBuf(p);
	if(A > 0xffffffff)
	{
		p->Length = 2;
		p->Value[1] = (RSAUNIT)(A >> 32);
	}
	else
	{
		p->Length = 1;
	}
	p->Value[0] = (RSAUNIT)A;
}
//数据相除
void DivByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A,LPBIGINT_PTR pResult
			  ,LPBIGINT_PTR pTmp1,LPBIGINT_PTR pTmp2,LPBIGINT_PTR pTmp3,LPBIGINT_PTR pTmp4)
{
	BIGINT_PTR tmp;
	int i,len;
	unsigned long long num,div;
	if(A->Length == 1)
	{
		DivLongByPtr(p, A->Value[0], pResult,pTmp1);
		return;
	}
	InitBigIntPtr(&tmp);
	CopyPtr(pTmp2, p);
	while(CmpByPtr(pTmp2,A) >= 0)
	{       
		div = pTmp2->Value[pTmp2->Length - 1];
		num = A->Value[A->Length - 1];
		len = pTmp2->Length - A->Length;
		if((div == num) && (len == 0))
		{
			MovInt64ToPtr(pTmp1, 1);
			break;
		}
		if((div <= num) && len)
		{
			--len;
			div=(div << 32) + pTmp2->Value[pTmp2->Length - 2];
		}
		div /= (num + 1);
		MovInt64ToPtr(pTmp3, div);
		if(len)
		{
			pTmp3->Length += len;
			for (i = pTmp3->Length - 1; i >= len; --i)
			{
				pTmp3->Value[i] = pTmp3->Value[i-len];
			}
			memset(pTmp3->Value,0,len * RSAUNIT_SIZE);
		}
		AddByPtr(pTmp1, pTmp3, pTmp1,&tmp);
		MulByPtr(A, pTmp3, pTmp4,&tmp);
		SubByPtr(pTmp2, pTmp4, pTmp2,&tmp);
	}
	MovByPtr(pResult, pTmp1);
	ReleaseBigIntPtr(&tmp);
}
//数据相乘
void MulLongByPtr(LPBIGINT_PTR p,unsigned int A,LPBIGINT_PTR pResult,LPBIGINT_PTR pTmp)
{
	unsigned long long mul;
	RSAUNIT carry = 0;
	int i;
	CopyPtr(pTmp, p);
	for(i = 0; i < p->Length; ++i)
	{
		mul = p->Value[i];
		mul = mul * A + carry;
		pTmp->Value[i] = (RSAUNIT)mul;
		carry = (RSAUNIT)(mul >> 32);
	}
	if(carry)
	{
		++pTmp->Length;
		pTmp->Value[pTmp->Length-1] = carry;
	}
	MovByPtr(pResult,pTmp);
}
//数据相乘
void MulByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A,LPBIGINT_PTR pResult,LPBIGINT_PTR pTmp)
{
	unsigned long long sum,mul=0;
	RSAUNIT carry=0;
	int i,j;
	if(A->Length == 1)
	{
		//		printf("MulPtr--call MulLong\r\n");
		MulLongByPtr(p, A->Value[0], pResult,pTmp);
		return;
	}
	ClearBigIntPtrBuf(pTmp);
	pTmp->Length = p->Length+A->Length-1;
	//	printf("A->Length=%d\r\n",A->Length);
	for (i = 0; i < pTmp->Length; ++i)
	{
		sum = carry;
		carry = 0;
		for (j = 0; j < A->Length; ++j)
		{
			if(((i - j) >= 0) && ((i - j) < p->Length))
			{
				//		printf("i=%d j=%d i-j=%d %d\r\n",i,j,i-j,(i-j>=0));
				mul = p->Value[i-j];
				//		printf("mul=%u A->Value[j]=%u j=%u\r\n",mul,A->Value[j],j);
				mul *= A->Value[j];
				carry += mul>>32;
				mul = mul&0xffffffff;
				//		printf("sum==%u\r\n",sum);
				sum += mul;
			}
		}
		carry += sum >> 32;
		pTmp->Value[i] = (RSAUNIT)sum;
	}
	if(carry)
	{
		++pTmp->Length;
		pTmp->Value[pTmp->Length - 1] = (RSAUNIT)carry;
	}
	MovByPtr(pResult, pTmp);
}
void ClearPtr(LPBIGINT_PTR p)
{
	p->Length = 1;
	memset(p->Value,0,BIGINT_BUF_SIZE);
}
//数据取模
void ModByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A,LPBIGINT_PTR pResult
			  ,LPBIGINT_PTR pTmp1,LPBIGINT_PTR pTmp2,LPBIGINT_PTR pTmp3)
{
	unsigned long long div,num;
	RSAUNIT carry = 0;
	int i,len,n;
	n = CmpByPtr(p, A);
	if(n < 0)
	{
		MovByPtr(pResult, p);//A);
		return ;
	}
	if(n == 0)
	{
		ClearPtr(pResult);
		return ;
	}
	CopyPtr(pTmp1, p);
	while(1)
	{
		div = pTmp1->Value[pTmp1->Length - 1];
		num = A->Value[A->Length - 1];
		len = pTmp1->Length-A->Length;
		if((div <= num) && len)
		{
			--len;
			div = (div << 32) + pTmp1->Value[pTmp1->Length - 2];
		}
		div /= (num + 1);
		MovInt64ToPtr(pTmp2, div);
		MulByPtr(A, pTmp2, pTmp2,pTmp3);
		if(len)
		{
			pTmp2->Length += len;
			//printf("Mod--Y.Length=%d len=%d\r\n",Y.Length,len);
			for(i = pTmp2->Length - 1; i >= len; --i)
			{
				pTmp2->Value[i] = pTmp2->Value[i - len];
			}
			memset(pTmp2->Value, 0, len * RSAUNIT_SIZE);
		}
		SubByPtr(pTmp1, pTmp2, pTmp1,pTmp3);
		n = CmpByPtr(pTmp1, A);
		if(n == 0)
		{
			ClearPtr(pResult);
			return ;
		}
		if(n < 0)
		{
			break;
		}
	}
	MovByPtr(pResult, pTmp1);
}
void SubByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A,LPBIGINT_PTR pResult,LPBIGINT_PTR pTmp)
{
	RSAUNIT carry = 0;
	unsigned long long num;
	int i;
	if(CmpByPtr(p,A) <= 0)
	{
		ClearPtr(pResult);
		return ;
	}
	CopyPtr(pTmp, p);
	//printf("SubPtr--p->Length=%d\r\n",p->Length);
	for (i = 0; i < p->Length; ++i)
	{
		if((p->Value[i] > A->Value[i]) || ((p->Value[i] == A->Value[i]) && (carry == 0)))
		{
			pTmp->Value[i] = p->Value[i] - carry - A->Value[i];
			carry = 0;
		}
		else
		{
			num = 0x100000000 + p->Value[i];
			pTmp->Value[i] = (RSAUNIT)(num - carry - A->Value[i]);
			carry = 1;
		}
	}
	while(pTmp->Value[pTmp->Length - 1] == 0)
	{
		--pTmp->Length;
	}
	MovByPtr(pResult, pTmp);
}
//比较数据大小
int CmpToPtr(LPBIGINT_PTR p,LPBIGINT A)
{
	int i;
	if(p->Length > A->Length)
	{
		return 1;
	}
	if(p->Length < A->Length)
	{
		return -1;
	}
	for(i = p->Length - 1; i >= 0; --i)//右边是高位
	{
		if(p->Value[i] > A->Value[i])
		{
			return 1;
		}
		if(p->Value[i] < A->Value[i])
		{
			return -1;
		}
	}
	return 0;
}
void DivLongByPtr(LPBIGINT_PTR p,unsigned int A,LPBIGINT_PTR pResult,LPBIGINT_PTR pTmp)
{
	unsigned long long div,mul;
	RSAUNIT carry=0;
	int i;
	CopyPtr(pTmp,p);
	if(pTmp->Length == 1)
	{
		pTmp->Value[0] /= A;
		MovByPtr(pResult,pTmp);
		return ;
	}
	for(i = pTmp->Length - 1; i >= 0; --i)
	{
		div = carry;
		div = (div << 32) + pTmp->Value[i];
		pTmp->Value[i] = (RSAUNIT)(div / A);
		mul = (div / A) * A;
		carry = (RSAUNIT)(div - mul);
	}
	if(pTmp->Value[pTmp->Length - 1] == 0)
	{
		--pTmp->Length;
	}
	MovByPtr(pResult,pTmp);
}
//比较数据大小
int CmpByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A)
{
	int i;
	if(p->Length > A->Length)
	{
		return 1;
	}
	if(p->Length < A->Length)
	{
		return -1;
	}
	for(i = p->Length - 1; i >= 0; --i)//右边是高位
	{
		if(p->Value[i] > A->Value[i])
		{
			return 1;
		}
		if(p->Value[i] < A->Value[i])
		{
			return -1;
		}
	}
	return 0;
}
//数据相加
void AddByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A,LPBIGINT_PTR pResult,LPBIGINT_PTR pTmp)
{
	int i;
	RSAUNIT carry = 0;
	unsigned long long sum = 0;
	CopyPtr(pTmp, p);
	if(pTmp->Length < A->Length)
	{
		pTmp->Length = A->Length;
	}
	for(i = 0; i < pTmp->Length; ++i)
	{
		sum = A->Value[i];
		sum += pTmp->Value[i] + carry;
		pTmp->Value[i] = (RSAUNIT)sum;
		carry = (RSAUNIT)(sum >> 32);
	}
	pTmp->Value[pTmp->Length] = carry;
	pTmp->Length += carry;
	MovByPtr(pResult, pTmp);
}
void EucByPtr(LPBIGINT p,LPBIGINT A,LPBIGINT pResult)
{
	BIGINT_PTR M,E,X,Y,I,J,mod,tmp1,tmp2,tmp3,tmp4;
	int x,y;
	InitBigIntPtr(&M);
	InitBigIntPtr(&E);
	InitBigIntPtr(&X);
	InitBigIntPtr(&Y);
	InitBigIntPtr(&I);
	InitBigIntPtr(&J);
	InitBigIntPtr(&mod);
	InitBigIntPtr(&tmp1);
	InitBigIntPtr(&tmp2);
	InitBigIntPtr(&tmp3);
	InitBigIntPtr(&tmp4);
	MovToPtr(&mod, A);
	MovToPtr(&M, A);
	MovToPtr(&E, p);
	MovInt64ToPtr(&X,0);
	MovInt64ToPtr(&Y,1);
	x = y = 1;
	//printf("Euc start\r\n");
	while((E.Length != 1) || (E.Value[0] != 0))
	{
		//printf("Euc--Set I=Div(M,E)\r\n");
		DivByPtr(&M, &E, &I,&tmp1,&tmp2,&tmp3,&tmp4);
		//printf("Set J\r\n");
		ModByPtr(&M, &E, &J,&tmp1,&tmp2,&tmp3);
		//printf("Set M\r\n");
		MovByPtr(&M, &E);
		//printf("Set E\r\n");
		MovByPtr(&E, &J);
		//printf(" Mov Y to J\r\n");
		CopyPtr(&J, &Y);
		//printf("Mov to Y\r\n");
		MulByPtr(&Y, &I, &Y,&tmp1);
		//printf("check x==y:%d\r\n",x==y);
		if(x == y)
		{ 
			if(CmpByPtr(&X, &Y) >= 0)
			{
				SubByPtr(&X, &Y, &Y,&tmp1);
			}
			else
			{
				SubByPtr(&Y, &X, &Y,&tmp1);
				y = 0;
			}
		}
		else
		{
			AddByPtr(&X, &Y, &Y,&tmp1);
			x = 1 - x;
			y = 1 - y;
		}
		MovByPtr(&X, &J);
	}
	if(x == 0)
	{
		SubByPtr(&mod, &X, &X,&tmp1);
	}
	MovFromPtr(pResult, &X);
	ReleaseBigIntPtr(&M);
	ReleaseBigIntPtr(&E);
	ReleaseBigIntPtr(&X);
	ReleaseBigIntPtr(&Y);
	ReleaseBigIntPtr(&I);
	ReleaseBigIntPtr(&J);
	ReleaseBigIntPtr(&mod);
	ReleaseBigIntPtr(&tmp1);
	ReleaseBigIntPtr(&tmp2);
	ReleaseBigIntPtr(&tmp3);
	ReleaseBigIntPtr(&tmp4);
}
void ModMulByPtr(LPBIGINT_PTR p,LPBIGINT_PTR A, LPBIGINT_PTR B,LPBIGINT_PTR pResult)
{
	int i,j;
	BIGINT_PTR X,P,tmp1,tmp2,tmp3;
	InitBigIntPtr(&X);
	InitBigIntPtr(&P);
	InitBigIntPtr(&tmp1);
	InitBigIntPtr(&tmp2);
	InitBigIntPtr(&tmp3);
	//printf("ModMul\r\n");
	MulLongByPtr(A, p->Value[p->Length-1], &X,&tmp1);
	ModByPtr(&X, B, &X,&tmp1,&tmp2,&tmp3);
	for(i = p->Length - 2; i >= 0; --i)
	{          
		for(j = X.Length;j > 0; --j)
		{
			X.Value[j] = X.Value[j - 1];
		}
		X.Value[0] = 0;
		++X.Length;
		//printf("ModMul for\r\n");
		MulLongByPtr(A, p->Value[i], &P,&tmp1);
		AddByPtr(&X, &P, &X,&tmp1);
		ModByPtr(&X, B, &X,&tmp1,&tmp2,&tmp3);
	}
	MovByPtr(pResult, &X);
	ReleaseBigIntPtr(&X);
	ReleaseBigIntPtr(&P);
	ReleaseBigIntPtr(&tmp1);
	ReleaseBigIntPtr(&tmp2);
	ReleaseBigIntPtr(&tmp3);
}

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
void PiaRsaEnc(char* pSrc, int pSrcLen, char* pKey, int pKeyLen, char* pMod, int pModLen, char** pResult, int* pResultLen)
{
	RESULTBUF output;
	InitResultBuf(&output, RSA_KEY_MAXLEN);
	//RsaRaw(EM_ENCODE, pSrc, pSrcLen, pKey, pKeyLen, pMod, pModLen, &output);
	RsaEncodePadding(1, pSrc, pSrcLen, pKey, pKeyLen, pMod, pModLen, &output);
	*pResult = output.buf;
	*pResultLen = output.dataLen;
}

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
void PiaRsaDec(char* pSrc, int pSrcLen, char* pKey, int pKeyLen, char* pMod, int pModLen, char** pResult, int* pResultLen)
{
	RESULTBUF output;
	InitResultBuf(&output, RSA_KEY_MAXLEN);
	//RsaRaw(EM_DECODE, pSrc, pSrcLen, pKey, pKeyLen, pMod, pModLen, &output);
	RsaDecode(pSrc, pSrcLen, pKey, pKeyLen, pMod, pModLen, &output);
	*pResult = output.buf;
	*pResultLen = output.dataLen;
}

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
void PiaRsaBase64Enc(char* pSrc, int pSrcLen, char* pKey, int pKeyLen, char* pMod, int pModLen, char** pResult, int* pResultLen)
{
	RESULTBUF output;
	InitResultBuf(&output, RSA_KEY_MAXLEN);
	//RsaRaw(EM_ENCODE, pSrc, pSrcLen, pKey, pKeyLen, pMod, pModLen, &output);
	RsaEncodePadding(1, pSrc, pSrcLen, pKey, pKeyLen, pMod, pModLen, &output);

	*pResultLen = output.dataLen * 2;
	*pResult = (char*)PiaMalloc(*pResultLen);
	if (*pResult)     
	{
		memset(*pResult,0,*pResultLen);    //add by fxm 20150205 需要清空一下，否则手机上字符串结尾可能会有乱码
	}
	*pResultLen = PiaBase64Encode(output.buf, output.dataLen, *pResult, *pResultLen);
	ReleaseResultBuf(&output);
}

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
void PiaRsaBase64Dec(char* pSrc, int pSrcLen, char* pKey, int pKeyLen, char* pMod, int pModLen, char** pResult, int* pResultLen)
{
	int len = pSrcLen;
	char *p = (char*)PiaMalloc(len);
	if (p)
	{
		RESULTBUF output;
		InitResultBuf(&output, RSA_KEY_MAXLEN);
		
		len = PiaBase64Decode(pSrc, p, len);
	
		//RsaRaw(EM_DECODE, p, len, pKey, pKeyLen, pMod, pModLen, &output);
		RsaDecode(p, len, pKey, pKeyLen, pMod, pModLen, &output);
		*pResult = output.buf;
		*pResultLen = output.dataLen;
		PiaFree(p);
	}
	else
	{
		*pResult = 0;
		*pResultLen = 0;
	}
}
  
/*
char HexChar(char c)
{
	if((c>='0')&&(c<='9'))
		return c-0x30;
	else if((c>='A')&&(c<='F'))
		return c-'A'+10;
	else if((c>='a')&&(c<='f'))
		return c-'a'+10;
	else 
		return 0x10;
}

//16进制字符串转数字
void h2s(const char *s, char *o) 
{
	// 计算代码共108个字符,应该还能精简
	for (int i = 0, c; c = s[i]; ++i)
	{
		(o[i/2] *= (i%2)*16)+=c<'0'?0:c<':'?c-'0':c<'A'?0:c<'G'?c-'7':c<'a'?0:c<'g'?c-'W':0;
	}
}

//将一个字符串作为十六进制串转化为一个字节数组，字节间可用空格分隔，
//返回转换后的字节数组长度，同时字节数组长度自动设置。
int Str2Hex(char* str, char* data)  
{
	int t,t1;
	int rlen=0,len=strlen(str);
	//data.SetSize(len/2);
	for(int i=0;i<len;)
	{
		char l,h=str[i];
		if (h==' ')
		{
			i++;
			continue;
		}
		i++;
		if(i>=len)
		{
			break;
		}
		l=str[i];
		t=HexChar(h);
		t1=HexChar(l);
		if((t==16)||(t1==16))
			break;
		else 
			t=t*16+t1;
		i++;
		data[rlen]=(char)t;
		rlen++;
	}
	return rlen;
}
*/

void HexByteToStr(BYTE pByte, char* pStr)
{
	int n10 = pByte / 16;
	int n = pByte % 16;
	if (n10 > 9)
	{
		*pStr = 'a' + (n10 - 10);
	}
	else
	{
		*pStr = '0' + n10;
	}
	++pStr;
	if (n > 9)
	{
		*pStr = 'a' + (n - 10);
	}
	else
	{
		*pStr = '0' + n;
	}
}
BYTE HexToByte(const char* shex)  
{   
	int   i,mid = 0;   
	int   len   =   2;       
	BYTE idec = 0;   
	for (i = 0; i < 2; ++i)   
	{   
		if (shex[i] >= '0' && shex[i] <= '9')
		{
			mid   =   shex[i] - '0';   
		}
		else   if (shex[i] >= 'a' && shex[i] <= 'f')
		{
			mid   =   shex[i]   - 'a' + 10;  
		}
		else   if (shex[i] >= 'A' && shex[i] <= 'F')
		{
			mid   =   shex[i]   - 'A'   + 10;  
		}
		else
		{
			break;       
		}
		mid <<= ((len - i - 1) << 2);   
		idec |= mid;       
	} 
	return idec;
}

char* HexToStr(BYTE *buf, int nLen, char* str)
{
	int i;
	for ( i = 0; i  < nLen; ++i  )
	{
		HexByteToStr(buf[i], str + i * 2);
	}
	*(str + nLen + nLen) = 0;
	return str;
}

//功能:将字符串表示的十六进制数转换到BYTE数组中
//返回值:buf的长度
int StrToHex(const char *str, int len, BYTE* buf)
{
	int i;
	int j = 0;
	for (i = 0; i < len; i++)
	{
		buf[j] = HexToByte(str + i);
		j++;
		i++;
	}
	return j;
}
//char* HexToStr(BYTE *buf,int nLen,char* str)
//{
//	int i,j=0;
//	char hex[3];
//	{
//		for ( i = 0; i  < nLen; ++i  )
//		{
//			sprintf_s(hex,3, ("%02x"), *(buf + i));
//			str[j++] = hex[0];
//			str[j++] = hex[1];
//		}
//	}
//	return str;
//}


//功能:将字符串表示的十六进制数转换到BYTE数组中
//返回值:buf的长度

//int StrToHex(const char *str, int len, BYTE* buf)
//{
//	int i = 0;
//	int j = 0;
//	char tmp[3] = {0};
//	int b = 0;
//	for(i = 0; i < len; i++)
//	{
//		memset(tmp,0,3);
//		tmp[0] = str[i];
//		tmp[1] = str[i+1];
//		sscanf_s(tmp,"%x",&b);
//		buf[j] = (BYTE)b;
//		j++;
//		i++;
//	}
//	return j;
//}
//
//
/*
功能:获取公钥和MOD
modPubKey:公钥和MOD组成的由DER编码后的bit流,已经base64解码了;
talLen:比特流的长度,以字节为单位;
mod:输出参数,Mod中存放指向存放模的缓冲区,以字符形式存储的;
modLen:输出参数,Mod的长度;
pubKey:输出参数,pubKey中存放指向存放公钥的缓冲区,以字符形式存储;
pkyLen:输出参数,pubKey的长度
返回值:成功返回1,失败返回0
*/
int BerGetRSAModPubKey(BYTE *modPubKey, int talLen, char** mod, int* modLen, char** pubKey, int* pkyLen)
{
	if(NULL != modPubKey && 0 != talLen)
	{
		int count = 0;
		BYTE* p = NULL;
		int i = 0;
		p = modPubKey;
		//找到第三个0x30
		while(i < talLen)
		{
			if(0x30 == p[i])
			{
				count++;
				if(3 == count)
				{
					break;
				}
			}
			i++;
		}
		if(i == talLen || 3 != count)
		{
			return 0;//解码失败
		}
		
		//找到0x02
		while(i < talLen)
		{
			if(0x02 == p[i])
			{
				break;
			}
			i++;
		}
		if(i < talLen)
		{
			int len1 = 0;
			BYTE* mod1 = NULL;
			i++;//此时p[i]指向表示长度的区域
			if(0x80 > p[i])
			{
				len1 = p[i];//p[i]这个字节是表示mod的长度
				i++;//此时p[i]指向MOD区域
			}
			else
			{
				memcpy(&len1,&p[i+1],p[i] - 0x80);//拷贝p[i] - 0x80个字节,这是长度区域所占的字节数
				i = i + 1 + p[i] - 0x80;//此时p[i]指向MOD区域
			}
			mod1 = (BYTE*)PiaMalloc(len1 + 1);
			if(mod1)
			{
				BYTE* mod1tmp = NULL;
				char* mod2 = NULL;
				int len1tmp = len1;
				memset(mod1,0,len1 + 1);
				memcpy(mod1,&p[i],len1);
				//去掉0x00
				mod1tmp = mod1;
				while(*mod1 == '\0')
				{
					mod1++;
					len1tmp--;
				}
				mod2 = (char*)PiaMalloc(len1tmp * 2 + 1);
				if(mod2)
				{
					memset(mod2,0,len1tmp * 2 + 1);
					HexToStr(mod1,len1tmp,mod2);
					*mod = mod2;
					*modLen = len1tmp * 2;
					PiaFree(mod1tmp);
					mod1tmp = NULL;
					i = i + len1;//此时p[i]应该为0x02,表示pubkey区域
					if(0x02 == p[i])
					{
						int len2 = 0;
						int len2tmp = 0;
						BYTE* pubkey1 = NULL;
						char* pubkey2 = NULL;
						i++;//此时p[i]指向表示pubkey长度的部分
						if(0x80 > p[i])
						{
							len2 = p[i];//p[i]这个字节是表示pubkey的长度
							i++;//此时p[i]指向pubkey区域
						}
						else
						{
							memcpy(&len2,&p[i+1],p[i] - 0x80);//拷贝p[i] - 0x80个字节,这是长度区域占的字节数
							i = i + 1 + p[i] - 0x80;//此时p[i]指向pubkey区域
						}
						pubkey1 = (BYTE*)PiaMalloc(len2 + 1);
						if(pubkey1)
						{
							BYTE* pubkey1tmp = NULL;
							memset(pubkey1,0,len2+1);
							memcpy(pubkey1,&p[i],len2);
							len2tmp = len2;
							//去掉0x00
							pubkey1tmp = pubkey1;
							while(*pubkey1 == '\0')
							{
								pubkey1++;
								len2tmp--;
							}
							pubkey2 = (char*)PiaMalloc(len2tmp * 2 + 1);
							memset(pubkey2,0,len2tmp * 2 + 1);
							HexToStr(pubkey1,len2tmp,pubkey2);
							PiaFree(pubkey1tmp);
							pubkey1tmp = NULL;
							*pubKey = pubkey2;
							*pkyLen = len2tmp * 2;
							return 1;
						}
					}
				}
			}
		}
	}
	return 0;
}
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
void BerRsaBase64Enc(char *pSrc, int iSrcLen, char* pModPubKey, int iModPubKeyLen, char** pEncRst, int* pEncRstLen)
{
	if(NULL != pSrc && 0 != iSrcLen && NULL != pModPubKey && 0 != iModPubKeyLen)
	{
		int ibase64declen = 0;
		BYTE* pModPubKeyBase64Dec = NULL;
		char* pMod = NULL;
		int iModLen = 0;
		char* pPubKey = NULL;
		int iPubKeyLen = 0;
		int iRst = 0;
		ibase64declen = iModPubKeyLen /4.0*3+10;
		pModPubKeyBase64Dec = (BYTE*)PiaMalloc(ibase64declen + 1);
		if(pModPubKeyBase64Dec)
		{
			memset(pModPubKeyBase64Dec,0,ibase64declen+1);
			ibase64declen = PiaBase64Decode(pModPubKey,(char*)pModPubKeyBase64Dec,ibase64declen);
			if(ibase64declen >= 0)
			{
				iRst = BerGetRSAModPubKey(pModPubKeyBase64Dec,ibase64declen,&pMod,&iModLen,&pPubKey,&iPubKeyLen);
				if(iRst)
				{
					PiaRsaBase64Enc(pSrc,iSrcLen,pPubKey,iPubKeyLen,pMod,iModLen,pEncRst,pEncRstLen);
				}
				if(pMod)
				{
					PiaFree(pMod);
					pMod = NULL;
				}
				if(pPubKey)
				{
					PiaFree(pPubKey);
					pPubKey = NULL;
				}
			}
		}
		if(pModPubKeyBase64Dec)
		{
			PiaFree(pModPubKeyBase64Dec);
			pModPubKeyBase64Dec = NULL;
		}
	}
}
/*
功能:获取私钥
参数:
priKeyBit:传入参数,berbon服务器传来的公钥,模,私钥在一起的bit流,已经base64解码了
talLen:传入参数,bit流的长度,以字节为单位
mod:传入参数,以字符串表示十六进制数的模
modLen:mod的长度
pubKey:传入参数,以字符串表示十六进制数的公钥
pbkyLen:pubKey的长度
返回值:成功返回1,失败返回0
*/
int BerGetRSAPriKey(BYTE* priKeyBit, int talLen, char* mod, int modLen, char** priKey, int* pikyLen)
{
	char* p = NULL;//把priKeyBit流以字符串形式存放,即以字符表示十六进制数
	char* pmod = NULL;
	int len = 0;
	BYTE* buf = NULL;
	int i = 0;
	p = (char*)PiaMalloc(talLen * 2 + 1);

	if(p)
	{
		memset(p, 0, talLen * 2 + 1);
		HexToStr(priKeyBit, talLen, p);
		pmod = strstr(p,mod);//指向p中mod的位置
		if(pmod)
		{
			pmod = pmod + modLen;//此时pmod指向p中公钥的位置,"02011"
			len = (int)((talLen * 2 - (pmod - p))/2);
			buf = (BYTE*)PiaMalloc(len + 1);
			if(buf)
			{
				memset(buf, 0, len + 1);
				StrToHex(pmod,len * 2,buf);
				PiaFree(p);
				p = NULL;
				i = 0;
				if(0x02 == buf[i])
				{
					i++;
					if(0x80 > buf[i])
					{
						memset(&len,0,4);
						len = buf[i];
						i++;
					}
					else
					{
						memset(&len,0,4);
						memcpy(&len,&buf[i+1],buf[i] - 0x80);
						i = i + 1 + buf[i] - 0x80;
					}
					i = i + len;
					if(0x02 == buf[i])//此时buf[i]指向比特流中私钥的部分
					{
						BYTE* prikey1 = NULL;
						char* prikey2 = NULL;
						i++;
						if(0x80 > buf[i])
						{
							memset(&len,0,4);
							len = buf[i];
							i++;
						}
						else
						{
							memset(&len,0,4);
							memcpy(&len,&buf[i+1],buf[i] - 0x80);
							i = i + 1 + buf[i] - 0x80;
						}
						
						prikey1 = (BYTE*)PiaMalloc(len + 1);
						if(prikey1)
						{
							BYTE* prikey1tmp = NULL;
							memset(prikey1, 0, len + 1);
							memcpy(prikey1,&buf[i],len);
							PiaFree(buf);
							buf = NULL;
							prikey1tmp = prikey1;
							while('\0' == *prikey1)
							{
								len--;
								prikey1++;
							}
							prikey2 =(char*)PiaMalloc(len * 2 + 1);
							if(prikey2)
							{
								memset(prikey2,0,len * 2 + 1);
								HexToStr(prikey1, len, prikey2);
								*priKey = prikey2;
								*pikyLen = len * 2;
								PiaFree(prikey1tmp);
								prikey1tmp = NULL;
								return 1;
							}
						}
					}
				}
			}
			
		}
	}
	return 0;
}
