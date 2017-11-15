#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include "includes/kernel.h"
#include "includes/allocator.h"
#include "includes/linkedlist.h"

// ----------------------------------------------
//   IMPLEMENTATION
// ----------------------------------------------

heap_t *heap; // Global pointer to the heap structure
uint overhead = sizeof(node_t) + sizeof(foot_t);

/* Initialises the heap structure. Must be called before any uses of 'mem_alloc' or 
 * 'mem_free'.
 */
void mem_init() {

    // Determine the number of blocks necessary to hold the heap
    int heap_size = sizeof(heap_t) + BIN_COUNT * sizeof(bin_t) + INIT_SIZE;
    int blocks = 1 + ((heap_size - 1) / MEM_BLOCK_SIZE); // ceil(heap_size / MEM_BLOCK_SIZE)

    // Allocate space for the heap structure (with initial block)
    heap = mem_block_alloc(blocks);
    if (heap == NULL) {
        printf("Insufficient memory to initialise the heap structure.\n");
        return;
    }

    // Initialise each of the bins, stored directly after the heap in memory
    uint bin_base_addr = ((uint) heap) + sizeof(heap_t);
    for (int i = 0; i < BIN_COUNT; i++) {
        heap->bins[i] = (bin_t *) (bin_base_addr + i * sizeof(bin_t));
    }

    // Create an initial node
    node_t *init_node = heap + sizeof(heap_t) + BIN_COUNT * sizeof(bin_t);
    int init_node_size = INIT_SIZE - overhead; // The size of initially allocatable memory
    
    create_node(init_node, init_node_size)

}

// Returns a pointer to contiguous memory of size at least 'n' bytes. Returns NULL
// if no memory is available or 'n' is zero. 'n' must be non-negative.
void *mem_alloc(size_t n) {
    assert(n >= 0);
    if (n == 0) return NULL;

    // Get the bin index of this chunk size
    uint index = get_bin_index(n);

    // Find an appropriately sized chunk
    bin_t *tmp = (bin_t *) heap->bins[index];
    node_t *found = get_fit(tmp, n);

    while (found == NULL) {
        tmp = heap->bins[++index];
        found = get_fit(tmp, n);

        // TODO: behaviour if the heap is totally full
    }

    // If the chunk is too large, we should split it in order to improve heap utilisation.
    if ((found->size - size) > (overhead + MIN_ALLOC_SIZE)) {

      // Create the new node after the split point
      node_t *split = ((char *) found + overhead) + n;
      int split_size = found->size - size - overhead;
      create_node(split, split_size);

      found->size = n;    // Set the found chunk's size
      create_foot(found); // Remake the foot
    }

    // Remove the node from its bin and set it to 'used'. Wipe its metadata, as this
    // is not needed while it is in use.
    delete_node(heap->bins[index], found);
    found->used = TRUE;
    found->prev = NULL; 
    found->next = NULL;

    return &found->next;
}

// Releases memory allocated by mem_alloc(). Does nothing if 'ptr' is NULL.
void mem_free(void *ptr) {
    if (ptr == NULL) return; // Do nothing

    bin_t *list;
    foot_t *new_foot, *old_foot;
    extern uint overhead;

    // Get the true head of the node by substracting the overhead
    node_t *node = (node_t *) ((char *) ptr - overhead);

    /* TODO: check if this node is at the start or end of a block
             if so, we do not need to attempt to coalesce neighouring nodes */
    
    // Get the next and previous nodes in the heap, in order to coalesce.
    node_t *next = (node_t *) ((char *) get_foot(node) + sizeof(footer_t));
    node_t *prev = (node_t *) * ((int *) ((char *) node - sizeof(footer_t)));
 
    if (!node->used) {
      coalesce(node);
    }

    if (!prev->used) {
      coalesce(prev);
      node = prev; // The new head of the node is now the at the 'previous' node
    }

    // Put this chunk in the appropriate bin
    node->used = FALSE;
    add_node(heap->bins[get_bin_index(node->size)], node);
}

// ----------------------------------------------
//   UTILITY FUNCTIONS
// ----------------------------------------------

/* Create a node with size 'size' at memory address 'addr', and add it to the
 * appropriate bin in the heap.  */
void create_node(node_t *node, int size) {
  assert(addr != NULL);
  assert(size > MIN_ALLOC_SIZE);

  node->size = size
  node->used = FALSE;

  create_foot(split);

  int index = get_bin_index(size);
  add_node(heap->bins[index], node);
}


/* Join this node with the next node in the heap */
void coalesce(node_t *node) {
  assert(node != NULL);

  // Remove the node from its bin
  bin_t *bin = heap->bins[get_bin_index(node->size)];
  remove_node(bin, next);

  // Recalculate the size of this node; create a footer
  node->size += overhead + node->next->size;
  create_foot(node);

}

/* Get the foot of a node */
void get_foot(node_t *node) {
  assert(node != NULL);
  return (foot_t *)((char *) node + sizeof(node_t) + node->size)
}

/* Create a foot for a node */
void create_foot(node_t *node) {
  assert(node != NULL)

    foot_t *foot = get_foot(node);
    foot->head = head;
}

/* Get the bin index of a node of a particular size */
int get_bin_index(size_t size) {
    assert(size > 0);

    int index = 0;
    size = size < 4 ? 4 : size;

    for (; size > 1; size = size >> 1) index++;

    index -= 2;

    if (index >= BIN_COUNT) index = BIN_COUNT - 1;
    return index;
}