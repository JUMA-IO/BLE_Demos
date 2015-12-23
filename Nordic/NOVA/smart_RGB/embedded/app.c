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

uint8_t rgb_color[4] = {255, 255, 0};

void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value)
{
  uint8_t onoff = value[0];
  uint8_t * rgb = value+1;                                     
  
  if(onoff == 1)
  {
    light_off();
    light_set_color(rgb);
    light_on();

  }
  else if(onoff == 0)
  {
    light_off();
  }
  else if(onoff == 2)
  {
    light_off();
    light_set_color(rgb_color);
    light_on();
  }
}

void ble_device_on_disconnect(uint8_t reason)
{
  ble_device_start_advertising();
}

void on_ready( )
{
  ble_device_set_name("RGB_Light"); 
	ble_device_set_advertising_interval(2000);
  ble_device_start_advertising();
	
  //uint8_t rgb_pin[4] = {7, 6, 5};
  //uint8_t rgb_pin[4] = {23, 21, 22};
  uint8_t rgb_pin[4] = {12, 11, 29};
  light_setup(rgb_pin, 1);
  
  light_off();
}



