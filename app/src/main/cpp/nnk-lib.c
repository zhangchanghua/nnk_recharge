//
// Created by Administrator on 2018/4/17.
//
#include <malloc.h>
#include <string.h>
#include "nnk-lib.h"
#include "dh.h"
#include "base64.h"
#include "yhbf.h"
#include <wchar.h>

const char *DHpPTable[] =
        {"C692DE995803B6023346BF46C02D9EB5DCB7ECD38F6C3F977D56483F3223377EB8C81715C6E5D6527D1298B9056BEEE4E5C697DF403C59F53665EAAD5A081FF8AE41A74D0A4DDE2DC8949486C4D54A73DAAE02DA7FAF1B6DDCB2B702ED4299447B23F3C503E7B7789A8CBCC0130D4928DFA14481397964C2E85BB21196E1D30F",
         "F67BB2C96A245A9814243F413225EFCB7A827113BFF5C57DFE50828FDC24CFE1F06B2603D25E801E2F39BEA40876792AEF7E67AD6E4A756925BE17270E798A1F4F60D921C5D967F9B93328D20EDD408F62C954E7227A39AF819AF068DA290DB51A599E309EF3B14FDFDCFD83C6EB1C7A0F5F0B0E3670FB90CF573DA68F1AA08F",
         "B9B8231EDB2B537B68C82D6B2323966167F1E961128ED4C80B1687035197F6A2B9060BDB5A3AF11006A505071708E7A8E11FF1AC539F3FE513AE9A44491F4FAC8A91260E11709485ABACF59D9D8E5D1C510FA5FB4245E5628C247002714190C6BF5A2D0CEE4E798B9D0BE83F4DC17BA29C6C5717B19EA20913FE85C091E5818F",
         "F8275C3AFCBE73845080566469429E3B913032367649F6AF28E97F2070FEDB51E0D245D5C7F6ED111C69A67649B8ED3D50B4F8105E1ABF994162B28CF45AC70990679A66689FE9F484A5808B210BBDD9E5A882C8D59ED3C64E994993A5B5A84D67DEA5BEC289011538A2BFE71D0D6FE4F09C0A8244095C349059B55DCE3B9097",
         "A0D83565B2AE5B3BA32502E512E504B8B371E5E561379D6877383CE383FDF615A12FF023055EDA8CE2822FBE63B105D86103708B82A4D95783876F58B416135A45ED22DF71483C5CDE6BD044299C2FEAC005749B692A58F18088F425657542B488519F098C2E16C3B358BA791E5350DE4671CD040DBE4C7137602A502FB5A827",
         "F74A98D2791F7A153A35597966B148A827147CB75966751FFAB5E9D90BEF05EEC4F3D097CC40639A38991B6EC9BE5D54EEAABB8E8FDB6D4B0B303BE692AC7D6AAAE584D4CB606D5DDA8599D3AD9AE0DCAD015E6AC3E1CD0C3EA0509C1D10B43E916924EA5EA82901794AC56C4924EB3B8037A68259F5EDEDCEBD87DA01184857",
         "9852D1C346DC1BE169D929CE61B277380F1AB51F6B92DBA554A1BD308D6D6EEE786A239927C0D8A9D0B97BE2A5F0E4DB1E14822A36F524AF001DAB2DA27FFB686C6A64B57BA3B1A87028D2959D23BD550210F7B0FBD04A5CF5FEF77FDA3C6711C37F1B68B2A99A588F0732C58599A8E523CB26E82900313906086264070C00AF",
         "AEBDE8A76D0FC8F5302E505F61957A34F55DF156F25AE175DE71314C635845D91D0C7DA3F9565736D5A9EA54C2826237EF76CC1E510430A44559A326F82C313408B09999BF4CE9D3FB1EBFFB9B67B1D831DF057A696DB7AB784452AD207E8C7B487872FD8D7A6319D270427B7C6A335710B59C8F0A067AFB1DA2E11AB641106B",
         "D7C966C8C3886569554C1164E3DB4518559B220D83E988686CBCF0A93336C660909C33B2FFDDDAFA8B2EB42CA371FFA72AA8905FA41E27D7B8399EF03B3BCE3608E1EA4BCDD8D4822A979C7DCA67F363532A68DCE8E7A0AE2C25F791A3321390480D1A8DAD6A1037FC8F576C9B75E3F787430553FE6D32BEFBE0D2A6F5532653",
         "F56E1B39AFFA28A03EE02074420409C6231E125BA0C379E84315EE9C6ACADEE110F370314B33026EF25243FEDC1AE9D63D4ABE62C9D682A38245BB09855391185354124A23FCBBE8DA67FAFF233375BA079221D2B9FD34F2B8C642E40AF8E792A81AB30F559A352F48C1214696C6DBC8977CEAA3156CE59E4F887F0DD2CE3933",
        };

//生成秘钥对
JNIEXPORT void JNICALL Java_com_nnk_rechargeplatform_utils_JniHelper_initKeys
        (JNIEnv *env, jobject obj) {

    char *pszTempPriKey = (char *) malloc(
            1024);     //2.0版本通信协议:第一次通信，保存DH生成的私钥，后面根据服务器发送过来的公钥，和此私钥生成ZZ，将ZZ存放在szTKey中，此处内存free
    int pszTempPriKeyLen;                //2.0版本通信协议:pDHKey密钥的长度

    int curRandNum = arc4random() % 10;

    char *pG = "2";

    int plen = (int) strlen(DHpPTable[curRandNum]), glen = (int) strlen(pG);

    char *pszTempPubKey = (char *) malloc(1024);//公钥Base64前
    int pszTempPubKeylen1 = 0; //公钥Base64前

    PiaGenDHKey(0, (char *) DHpPTable[curRandNum], plen, pG, glen, pszTempPriKey,
                &pszTempPriKeyLen, pszTempPubKey, &pszTempPubKeylen1);
    //公钥
    char *pszPubKey = (char *) malloc(1024);
    //私钥
    char *pszPriKey = (char *) malloc(1024);
    //公钥
    PiaBase64Encode(pszTempPubKey, pszTempPubKeylen1, pszPubKey, 1024);

    //私钥
    PiaBase64Encode(pszTempPriKey, pszTempPriKeyLen, pszPriKey, 1024);

    jclass cls = (*env)->GetObjectClass(env, obj);
    if (cls) {
        jfieldID pPublicKey = (*env)->GetFieldID(env, cls, "pPublicKey", "Ljava/lang/String;");
        (*env)->SetObjectField(env, obj, pPublicKey, (*env)->NewStringUTF(env, pszPubKey));
        jfieldID pPrivateKey = (*env)->GetFieldID(env, cls, "pPrivateKey", "Ljava/lang/String;");
        (*env)->SetObjectField(env, obj, pPrivateKey, (*env)->NewStringUTF(env, pszPriKey));
        jfieldID dhGroupID = (*env)->GetFieldID(env, cls, "dhGroupID", "I");
        (*env)->SetIntField(env, obj, dhGroupID, curRandNum);
    }
}

//加密解密1加密，0解密
JNIEXPORT jbyteArray JNICALL Java_com_nnk_rechargeplatform_utils_JniHelper_BlowFishWithBase64
        (JNIEnv *env, jobject obj, jint type, jstring keyData, jstring originData) {
    const char *origin = (*env)->GetStringUTFChars(env, originData, JNI_FALSE);
    const char *key = (*env)->GetStringUTFChars(env, keyData, JNI_FALSE);

    char *responseJSONStr = (char *) malloc(1024 * 10);
    BlowFishWithBase64(origin, (int) type, key, responseJSONStr, 1024 * 10);

    jbyteArray arry = NULL;
    arry = (*env)->NewByteArray(env, strlen(responseJSONStr));
    (*env)->SetByteArrayRegion(env, arry, 0, strlen(responseJSONStr), (jbyte *) responseJSONStr);
    (*env)->ReleaseStringUTFChars(env, originData, origin);
    (*env)->ReleaseStringUTFChars(env, keyData, key);
    free(responseJSONStr);
    return arry;
}


//根据私钥 服务器公钥 生成新的 t_key
JNIEXPORT jstring JNICALL Java_com_nnk_rechargeplatform_utils_JniHelper_PiaGenDHZZ
        (JNIEnv *env, jobject obj, jstring privateKey, jint curRandNum, jstring publicKey) {
    char *ZZ_buf = (char *) malloc(1024);
    int pZZLen = 0;
    const char *private = (*env)->GetStringUTFChars(env, privateKey, JNI_FALSE);
    const char *public = (*env)->GetStringUTFChars(env, publicKey, JNI_FALSE);
    char *cMypPrivateKey11 = (char *) malloc(1024);
    int MypPrivateKeylen = PiaBase64Decode(private, cMypPrivateKey11, 1024);
    char *cnetpDHKey11 = (char *) malloc(1024);
    int netpDHKeylen = PiaBase64Decode(public, cnetpDHKey11, 1024);
    PiaGenDHZZ(0, (char *) DHpPTable[curRandNum],
               (int) strlen(DHpPTable[curRandNum]),
               cMypPrivateKey11, MypPrivateKeylen, cnetpDHKey11, netpDHKeylen,
               ZZ_buf, &pZZLen);
    char *pKey = (char *) malloc(1024);
    PiaBase64Encode(ZZ_buf, pZZLen, pKey, 1024);
    jstring result = (*env)->NewStringUTF(env, pKey);
    (*env)->ReleaseStringUTFChars(env, privateKey, private);
    (*env)->ReleaseStringUTFChars(env, publicKey, public);
    return result;
}



