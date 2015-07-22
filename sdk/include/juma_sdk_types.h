/*
  Copyright 2014-2015 juma.io

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

#ifndef _PSM_TYPES_H_
#define _PSM_TYPES_H_

#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

typedef void (*function_t)(void* args);

#define SERIAL_FRAME_LENGTH 32

#define TICKS_1_SECOND 1024
#define TICKS_1_MINUTE (TICKS_1_SECOND * 60)

enum {
    GPIO_OUTPUT = 1,
    GPIO_INPUT_NOPULL,
    GPIO_INPUT_PULLUP,
    GPIO_INPUT_PULLDOWN,
};

enum {
    GPIO_RISING = 1,
    GPIO_FALLING,
};

enum {
    UART_BAUD_9600 = 1,
};

enum {
    UART_8N1 = 1,
};

enum {
    BLE_DEVICE_NORMAL,
    BLE_DEVICE_LINK_LOST,
};

// Per Pixel Operations
enum {
    GFX_SET_BLACK,
    GFX_SET_WHITE,
    GFX_INVERT,
};

#define GFX_BLACK_IS_1   0x01

typedef struct _uart_config_t {
    uint32_t speed;
    uint8_t  mode;
    uint8_t  flow_control;
    uint8_t  has_irq_pin;
    uint8_t  rx_pin, tx_pin, rts_pin, cts_pin, irq_pin;
} uart_config_t;

typedef struct _light_config_t {
    uint8_t pins[4]; // pins for RGBW
} light_config_t;

typedef struct _adc_result_t {
    uint16_t value;
} adc_result_t;

typedef struct {
	const uint8_t *font_table;
    uint8_t space;
	uint8_t width;
	uint8_t height;
	char start_char;
	char end_char;
} gfx_font_t;

#endif //_PSM_TYPES_H_
