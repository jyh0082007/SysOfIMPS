/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_imps_media_video_codec_NativeH264Encoder */

#ifndef _Included_NativeH264Encoder
#define _Included_NativeH264Encoder
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_imps_media_video_codec_NativeH264Encoder
 * Method:    InitEncoder
 * Signature: (IIF)I
 */
JNIEXPORT jint JNICALL Java_com_imps_media_video_codec_NativeH264Encoder_InitEncoder
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     com_imps_media_video_codec_NativeH264Encoder
 * Method:    EncodeFrame
 * Signature: ([BJ)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_imps_media_video_codec_NativeH264Encoder_EncodeFrame
  (JNIEnv *, jclass, jbyteArray, jlong);

/*
 * Class:     com_imps_media_video_codec_NativeH264Encoder
 * Method:    DeinitEncoder
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_imps_media_video_codec_NativeH264Encoder_DeinitEncoder
  (JNIEnv *, jclass);

/*
 * Class:     com_imps_media_video_codec_NativeH264Encoder
 * Method:    getLastEncodeStatus
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_imps_media_video_codec_NativeH264Encoder_getLastEncodeStatus
  (JNIEnv *env, jclass clazz);

#ifdef __cplusplus
}
#endif
#endif