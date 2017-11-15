#ifndef ALLOCATOR
#define ALLOCATOR

#include <stdint.h>
#include <stddef.h>
#include <assert.h>
#include <stdbool.h>

#define INIT_SIZE 0x100  // The initial size of the heap
#define BIN_COUNT 9      // The number of bins to use in the heap
#define MIN_ALLOC_SIZE 1 // The minimum size of allocatable memory

typedef unsigned int uint;

// The header of an allocated memory block
typedef struct node_t {
  bool used;
  uint size;
  struct node_t* next;
  struct node_t* prev;
} node_t;

// The footer of an allocated memory block
typedef struct {
  node_t* head; // pointer to the header
} foot_t;

// Pointer to a doubly-linked list of nodes
typedef struct {
  node_t* head;
} bin_t;

// The heap metadata: pointers to all of the bins
typedef struct {
  bin_t *bins[BIN_COUNT];
} heap_t;

extern uint overhead;

void mem_init(void);
void *mem_alloc(size_t n);
void mem_free(void* ptr);

int get_bin_index(size_t size);
void create_foot(node_t *head);

#endif