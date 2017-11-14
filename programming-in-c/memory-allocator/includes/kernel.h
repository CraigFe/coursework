#ifndef KERNEL
#define KERNEL

// Size of a block, as used by mem_block_alloc().
#define MEM_BLOCK_SIZE 4096

void *mem_block_alloc(size_t n);
void mem_block_free(void *ptr);

#endif