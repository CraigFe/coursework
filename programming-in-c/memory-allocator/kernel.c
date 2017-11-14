#include <stdlib.h>
#include <assert.h>
#include <memory.h>
#include <stdbool.h>

#include "includes/kernel.h"

// ----------------------------------------------
//   KERNEL PROVIDED FUNCTIONS
// ----------------------------------------------

// Set this to true in a test to simulate out of memory.
bool memory_exhausted = false;

// Returns a pointer to contiguous memory of size at least 'n' blocks.
// 'n' must be greater than 0. Returns NULL if no memory is available.
void *mem_block_alloc(size_t n) {
    assert(n > 0);
    if (memory_exhausted) {
        return NULL;
    }
    return malloc(n * MEM_BLOCK_SIZE);
}

// Releases memory allocated by mem_block_alloc(). 'ptr' MUST NOT be NULL.
void mem_block_free(void *ptr) {
    assert(ptr != NULL);
    free(ptr);
}