#ifndef _APP_H_
#define _APP_H_
#include "bluenrg_sdk_api.h"
#include "x_nucleo_iks01a1.h"
#include "x_nucleo_iks01a1_hum_temp.h"
#include "hts221.h"

void hum_temp_monitor_init(void);
void read_temp_hum(void);
#endif //_APP_H_