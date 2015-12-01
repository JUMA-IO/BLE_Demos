#ifndef _QUEUE_H_
#define _QUEUE_H_

#include "juma_sdk_api.h"

void queue_init(void);
uint8_t queue_in(uint8_t value);
uint8_t queue_out(uint8_t *value);

#endif
