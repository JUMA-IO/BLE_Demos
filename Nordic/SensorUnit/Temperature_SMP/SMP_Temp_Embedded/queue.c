#include "queue.h"

#define QUEUE_BODY_MAX 2048

uint8_t queue_body[QUEUE_BODY_MAX];

uint16_t queue_in_step;
uint16_t queue_out_step;
uint16_t queue_count;

static void queue_in_step_next()
{
  queue_in_step ++;
  
  if(queue_in_step >= QUEUE_BODY_MAX)
  {
    queue_in_step = 0;
  }
}

static void queue_out_step_next()
{
  queue_out_step ++;
  
  if(queue_out_step >= QUEUE_BODY_MAX)
  {
    queue_out_step = 0;
  }
}

static void queue_count_add()
{
  if(queue_count < QUEUE_BODY_MAX)
  {
    queue_count ++;
  }
}

static void queue_count_min()
{
  if(queue_count > 0)
  {
    queue_count --;
  }
}

void queue_init(void)
{
  queue_in_step = 0;
  queue_out_step = 0;
  queue_count = 0;
}

uint8_t queue_in(uint8_t value)
{
  if(queue_count < QUEUE_BODY_MAX)
  {
    queue_body[queue_in_step] = value;
    
    queue_in_step_next();
    queue_count_add();
  }
  else
  {
    queue_body[queue_in_step] = value;
    
    queue_out_step_next();
    queue_in_step_next();
  }
  
  return 0;
}

uint8_t queue_out(uint8_t *value)
{
  if(queue_count > 0)
  {
    *value = queue_body[queue_out_step];
    
    queue_count_min();
    queue_out_step_next();
    
    return 0;
  }
  else
  { 
    return 1;
  }
}

