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


void left_cycle_front();
void left_cycle_back();
void left_cycle_stop();

void right_cycle_front();
void right_cycle_back();
void right_cycle_stop();

void biggun_up_down_start();
void biggun_up_down_stop();

void biggun_left();
void biggun_stop();
void biggun_right();


void on_ready()
{

  left_cycle_stop();
  right_cycle_stop();
  biggun_up_down_stop();
  biggun_stop();
  
  ble_device_set_name("JUMA_Tank");
  ble_device_start_advertising();
}

void ble_device_on_connect(void)
{
  gpio_setup(2, GPIO_OUTPUT);
  gpio_write(2, 0);
}


void ble_device_on_disconnect(uint8_t reason)
{
  gpio_setup(2, GPIO_OUTPUT);
  gpio_write(2, 1);
  
  left_cycle_stop();
  right_cycle_stop();
  biggun_up_down_stop();
  biggun_stop();
  
  ble_device_start_advertising();
}


void left_cycle_front()
{
  gpio_setup(25, GPIO_OUTPUT);
  gpio_setup(26, GPIO_OUTPUT);
  
  gpio_write(25, 1);
  gpio_write(26, 0);
}

void left_cycle_back()
{
  gpio_setup(25, GPIO_OUTPUT);
  gpio_setup(26, GPIO_OUTPUT);
  
  gpio_write(25, 0);
  gpio_write(26, 1);
}

void left_cycle_stop()
{
  gpio_setup(25, GPIO_OUTPUT);
  gpio_setup(26, GPIO_OUTPUT);
  
  gpio_write(25, 0);
  gpio_write(26, 0);
}

void right_cycle_front()
{
  gpio_setup(6, GPIO_OUTPUT);
  gpio_setup(5, GPIO_OUTPUT);
  
  gpio_write(6, 1);
  gpio_write(5, 0);
}

void right_cycle_back()
{
  gpio_setup(6, GPIO_OUTPUT);
  gpio_setup(5, GPIO_OUTPUT);
  
  gpio_write(6, 0);
  gpio_write(5, 1);
}

void right_cycle_stop()
{
  gpio_setup(6, GPIO_OUTPUT);
  gpio_setup(5, GPIO_OUTPUT);
  
  gpio_write(6, 0);
  gpio_write(5, 0);
}

void biggun_left()
{
  gpio_setup(3, GPIO_OUTPUT);
  gpio_setup(4, GPIO_OUTPUT);
  
  gpio_write(3, 1);
  gpio_write(4, 0);
}

void biggun_stop()
{
  gpio_setup(3, GPIO_OUTPUT);
  gpio_setup(4, GPIO_OUTPUT);
  
  gpio_write(3, 0);
  gpio_write(4, 0);
}

void biggun_right()
{
  gpio_setup(3, GPIO_OUTPUT);
  gpio_setup(4, GPIO_OUTPUT);
  
  gpio_write(3, 0);
  gpio_write(4, 1);
}

void biggun_up_down_start()
{
  gpio_setup(27, GPIO_OUTPUT);
  gpio_write(27, 1);
}

void biggun_up_down_stop()
{
  gpio_setup(27, GPIO_OUTPUT);
  gpio_write(27, 0);
}

void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value)
{
  switch(value[0])
  {
    case 0x00: left_cycle_stop(); break;
    case 0x01: left_cycle_front(); break;
    case 0x02: left_cycle_back(); break;
    
    case 0x10: right_cycle_stop(); break;
    case 0x11: right_cycle_front(); break;
    case 0x12: right_cycle_back(); break;
    
    case 0x20: biggun_up_down_stop(); break;
    case 0x21: biggun_up_down_start(); break;
    
    case 0x30: biggun_stop(); break;
    case 0x31: biggun_left(); break;
    case 0x32: biggun_right(); break;
    
    default : break;
  }
}

