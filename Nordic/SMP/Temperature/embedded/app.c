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
#include "queue.h"

#define Status_LED 5

uint16_t capture_speed;

uint8_t send_en;

void temp_send_slow(void * args)
{
  uint8_t value;
  uint8_t data[2];
  if(send_en)
  {
    if(queue_out(&value))
    {
      
    }
    else
    {
      data[0] = value;
      ble_device_send(0x01, 1, data);
    }
    
    run_after_delay(temp_send_slow, NULL, 500);
  }
}

#define DATA_PACK_MAX 18

void temp_send_fast(void * args)
{
  uint8_t len = 0;
  uint8_t value;
  uint8_t data[DATA_PACK_MAX];
  
  if(send_en)
  {
    while(len < DATA_PACK_MAX)
    {
      if(queue_out(&value))
      {
        break;
      }
      else
      {
        
        data[len] = value;
        len ++;
      }
    }
    
    ble_device_send(0x00, len, data);
    
    if(len >= DATA_PACK_MAX)
    {
      run_after_delay(temp_send_fast, NULL, 20);
    }
    else
    {
      run_after_delay(temp_send_slow, NULL, 20);
    }
  }
}


void temp_capture_process(void * args)
{
  uint8_t temp;
  
  temp = get_temperature();
  queue_in(temp);
  
  run_after_delay(temp_capture_process, NULL, capture_speed);
}


void on_ready()
{
  queue_init();
  capture_speed = 5000;
  send_en = 0;
  
  run_after_delay(temp_capture_process, NULL, 200);
  
  ble_device_set_name("JUMA_Temp");
  //ble_device_set_advertising_interval(2000);
  ble_device_start_advertising();
	
	gpio_setup(Status_LED, GPIO_OUTPUT);
	gpio_write(Status_LED, 1);
}

void ble_device_on_connect(void)
{
	gpio_write(Status_LED, 0);
  send_en = 1;
  capture_speed = 5000;
  
  run_after_delay(temp_send_fast, NULL, 100);
}


void start_advertising(void * args)
{
  ble_device_start_advertising();
}

void ble_device_on_disconnect(uint8_t reason)
{
	gpio_write(Status_LED, 1);
  send_en = 0;
  capture_speed = 5000;
  
  run_after_delay(start_advertising, NULL, 1000);
}



void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value)
{
  uint8_t data;
  if(value[0] == 0x00)
  {
    queue_in(value[1]);
  }
  else if(value[0] == 0x01)
  {
    queue_out(&data);
  }
}

