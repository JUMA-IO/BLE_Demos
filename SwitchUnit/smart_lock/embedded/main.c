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

#include "juma_sdk_api.h"

#define SWITCH_PIN 28
#define SWITCH_LED 2


void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value)
{
	switch(value[0])
	{
		case 0:
			gpio_write(SWITCH_PIN, 0);
			break;
		case 1:
			gpio_write(SWITCH_PIN, 1);
			break;
		default:
			break;
		
	}
}

void ble_device_on_disconnect(uint8_t reason)
{
	ble_device_set_advertising_interval(250);
  ble_device_start_advertising();
	gpio_write(SWITCH_LED, 1);
}

void ble_device_on_connect(void)
{
	gpio_write(SWITCH_LED, 0);
}



void on_ready()
{
  ble_device_set_name("NovaFan");
	ble_device_set_advertising_interval(250);
  ble_device_start_advertising();
	
	gpio_setup(SWITCH_PIN, GPIO_OUTPUT);
	gpio_setup(SWITCH_LED, GPIO_OUTPUT);
	gpio_write(SWITCH_LED, 1);

}



