#include <jni.h>
#include <vector>
#include "lame.h"

static lame_global_flags* lameClient = nullptr;
static int currentChannels = 2;

extern "C"
JNIEXPORT void JNICALL
Java_com_video_downloader_data_encoder_audio_NativeLameMp3Encoder_nativeInit(
        JNIEnv* env,
jobject thiz,
        jint sampleRate,
jint channelCount,
        jint bitrateKbps
) {
if (lameClient != nullptr) {
lame_close(lameClient);
lameClient = nullptr;
}

currentChannels = channelCount;

lameClient = lame_init();
lame_set_in_samplerate(lameClient, sampleRate);
lame_set_out_samplerate(lameClient, sampleRate);
lame_set_num_channels(lameClient, channelCount);
lame_set_brate(lameClient, bitrateKbps);
lame_set_quality(lameClient, 5);

if (channelCount == 1) {
lame_set_mode(lameClient, MONO);
} else {
lame_set_mode(lameClient, JOINT_STEREO);
}

lame_init_params(lameClient);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
        Java_com_video_downloader_data_encoder_audio_NativeLameMp3Encoder_nativeEncode(
        JNIEnv* env,
        jobject thiz,
jshortArray pcmArray,
        jint samplesPerChannel
) {
if (lameClient == nullptr || pcmArray == nullptr || samplesPerChannel <= 0) {
return env->NewByteArray(0);
}

jsize pcmLength = env->GetArrayLength(pcmArray);
jshort* pcm = env->GetShortArrayElements(pcmArray, nullptr);

int mp3BufferSize = static_cast<int>(1.25 * samplesPerChannel + 7200);
std::vector<unsigned char> mp3Buffer(mp3BufferSize);

int encodedBytes = 0;

if (currentChannels == 1) {
encodedBytes = lame_encode_buffer(
        lameClient,
        pcm,
        nullptr,
        samplesPerChannel,
        mp3Buffer.data(),
        mp3BufferSize
);
} else {
encodedBytes = lame_encode_buffer_interleaved(
        lameClient,
        pcm,
        samplesPerChannel,
        mp3Buffer.data(),
        mp3BufferSize
);
}

env->ReleaseShortArrayElements(pcmArray, pcm, JNI_ABORT);

if (encodedBytes <= 0) {
return env->NewByteArray(0);
}

jbyteArray result = env->NewByteArray(encodedBytes);
env->SetByteArrayRegion(
        result,
0,
encodedBytes,
reinterpret_cast<jbyte*>(mp3Buffer.data())
);

return result;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
        Java_com_video_downloader_data_encoder_audio_NativeLameMp3Encoder_nativeFlush(
        JNIEnv* env,
        jobject thiz
) {
if (lameClient == nullptr) {
return env->NewByteArray(0);
}

std::vector<unsigned char> mp3Buffer(7200);

int encodedBytes = lame_encode_flush(
        lameClient,
        mp3Buffer.data(),
        static_cast<int>(mp3Buffer.size())
);

if (encodedBytes <= 0) {
return env->NewByteArray(0);
}

jbyteArray result = env->NewByteArray(encodedBytes);
env->SetByteArrayRegion(
        result,
0,
encodedBytes,
reinterpret_cast<jbyte*>(mp3Buffer.data())
);

return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_video_downloader_data_encoder_audio_NativeLameMp3Encoder_nativeClose(
        JNIEnv* env,
jobject thiz
) {
if (lameClient != nullptr) {
lame_close(lameClient);
lameClient = nullptr;
}
}