#ifndef ALLOCATOR
#define ALLOCATOR

#include <stdint.h>
#include <stddef.h>
#include <assert.h>
#include <stdbool.h>

#define INIT_SIZE 0x100
#define BIN_COUNT 9

typedef unsigned int uint;

typedef struct node_t {
  bool used;
  uint size;
  struct node_t* next;
  struct node_t* prev;
} node_t;

typedef struct {
  node_t* head;
} bin_t;

typedef struct {
  node_t* head;
} foot_t;

typedef struct {
  node_t *start;
  node_t *end;
  bin_t *bins[BIN_COUNT];
} heap_t;

extern uint overhead;

void mem_init(void);
void *mem_alloc(size_t n);
void mem_free(void* ptr);

int get_bin_index(size_t size);
void create_foot(node_t *head);

#endif