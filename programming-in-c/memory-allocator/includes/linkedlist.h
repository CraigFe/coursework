#ifndef LINKEDLIST
#define LINKEDLIST

#include "allocator.h"
#include <stdint.h>

void add_node(bin_t *bin, node_t *node);
void delete_node(bin_t *bin, node_t *node);

node_t *get_fit(bin_t *bin, size_t size);
node_t *get_last(bin_t *bin);

#endif