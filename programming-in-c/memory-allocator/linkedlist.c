#include <stdio.h>
#include <assert.h>

#include "includes/linkedlist.h"

/* Add a node to a bin, in sorted order
 *  PRECONDITIONS: 'node' and 'bin' must be non-null */
void add_node(bin_t *bin, node_t *node) {
    assert(bin != NULL);
    assert(node != NULL);

    node->next = NULL;
    node->prev = NULL;
    printf("Adding node with head %p\n", (void *) bin->head);

    // The bin is currently empty
    if (bin->head == NULL) {
        bin->head = node;
        return;
    }

    // Insert after the last node which is smaller than this one
    node_t *current = bin->head;
    node_t *previous = NULL;

    printf("Current: %p", (void *) current);
    printf("Current size: %d", current->size);

    while (current != NULL && current->size <= node->size) {
        previous = current;
        current = current->next;
    }

    printf("Current value: %p\n", (void *) current);
    printf("Previous value: %p\n", (void *) previous);

    if (current == NULL) { // End of list
        previous->next = node;
        node->prev = previous;

    } else if (previous == NULL) { // Head is the only element
        node->next = bin->head;
        bin->head->prev = node;
        bin->head = node;

    } else { // More than one element
        node->next = current;
        previous->next = node;

        node->prev = previous;
        current->prev = node;
    }
}

/* Remove a node from a bin.
 *  PRECONDITIONS: 'node' must be non-null. */
void delete_node(bin_t *bin, node_t *node) {
    assert(node != NULL);

    if (bin->head == NULL) return; // Empty list

    if (bin->head == node) { // Node is at the head of the list
        bin->head = bin->head->next;
        return;
    }

    for (node_t *tmp = bin->head->next; tmp != NULL; tmp = tmp->next) {
        if (tmp != node) continue; // Keep looking

        tmp->prev->next = tmp->next;

        if (tmp->next != NULL) // If not last node
            tmp->next->prev = tmp->prev;
    }
}

/* Get the first node from a bin which is sufficiently large
 *  PRECONDITIONS: 'bin' must be non-null; 'size' must be greater than 0 */
node_t *get_fit(bin_t *bin, size_t size) {
    assert(bin != NULL)
    assert(size > 0);

    for (node_t *tmp = bin->head; tmp != NULL; tmp = tmp->next) {
        if (tmp->size >= size) return tmp;
    }

    return NULL;
}

/* Get the last node from a bin
 *  PRECONDITIONS: 'bin' must be non-null */
node_t *get_last(bin_t *bin) {

    node_t *t = bin->head;
    while (t->next != NULL) t = t->next;
    return t;

}