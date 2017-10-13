#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include "list.h"

List *cons(int head, List *tail) { 
  /* malloc() will be explained in the next lecture! */
  List *cell = malloc(sizeof(List));
  cell->head = head;
  cell->tail = tail;
  return cell;
}

/* Functions for you to implement */

int sum(List *list) {
  int i = 0;
  while (list != NULL) {
    i += list->head;
    list = list->tail;
  }
  return i;
}

void iterate(int (*f)(int), List *list) {
  while (list != NULL) {
    list->head = f(list->head);
    list = list->tail;
  }
}

void print_list(List *list) {

  printf("[");
  for (bool first = true; list != NULL; first = false) {
    if (first) printf("%d", list->head);
    else printf(", %d",list->head);
    list = list->tail;
  }
  printf("]\n");
}

/**** CHALLENGE PROBLEMS ****/

List* merge(List* list1, List* list2) { 
  List* merged = NULL;
  List** tail = &merged;

  while (list1 && list2) {
    if (list1->head < list2->head) {
      *tail = list1;
      list1 = list1->tail;
    } else {
      *tail = list2;
      list2 = list2->tail;
    }

    tail = &((*tail)->tail);
  }
  *tail = list1 ? list1 : list2;
  return merged;
}

void split(List* list, List** list1, List** list2) { 
  List* slow = list;
  List* fast = list;

  while (fast && fast->tail) {
    slow = slow->tail;
    fast = fast->tail->tail;
  }

  *list1 = list;
  *list2 = slow->tail;
  slow->tail = NULL;
}

/* You get the mergesort implementation for free. But it won't
   work unless you implement merge() and split() first! */

List *mergesort(List *list) { 
  if (list == NULL || list->tail == NULL) { 
    return list;
  } else { 
    List *list1;
    List *list2;
    split(list, &list1, &list2);
    list1 = mergesort(list1);
    list2 = mergesort(list2);
    return merge(list1, list2);
  }
}
