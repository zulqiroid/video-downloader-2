cmake_minimum_required(VERSION 3.22.1)

project(lame_mp3_encoder)

add_library(
        lame_mp3_encoder
        SHARED
        lame_jni.cpp

        lame/bitstream.c
        lame/encoder.c
        lame/fft.c
        lame/gain_analysis.c
        lame/id3tag.c
        lame/lame.c
        lame/newmdct.c
        lame/presets.c
        lame/psymodel.c
        lame/quantize.c
        lame/quantize_pvt.c
        lame/reservoir.c
        lame/set_get.c
        lame/tables.c
        lame/takehiro.c
        lame/util.c
        lame/vbrquantize.c
        lame/VbrTag.c
        lame/version.c
)

target_include_directories(
        lame_mp3_encoder
        PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/lame
)

target_compile_definitions(
        lame_mp3_encoder
        PRIVATE
        HAVE_CONFIG_H=1
)

find_library(
        log-lib
        log
)

target_link_libraries(
        lame_mp3_encoder
        m
        ${log-lib}
)