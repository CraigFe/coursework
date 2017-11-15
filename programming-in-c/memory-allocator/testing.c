#include <assert.h>
#include <stdbool.h>

#include "includes/kernel.h"

// ----------------------------------------------
//   TESTING
// ----------------------------------------------
extern bool memory_exhausted;

#define TEST_COUNT 8

int main() {
    printf("Initialising memory");

    // Initialise the memory
    mem_init();

    printf("Memory has been initialised... \nRunning all tests:");
    int passedCount = 0; // Running total of the number of passes

    // Run all of the tests
    for (int i = 0; i < TEST_COUNT; i++) {
      printf("Running test #%d...", i);
      bool result = test(i);
      
      if (result) {
        printf("   Passed.\n");
        passedCount++;
      } else {
        printf("   Failed!\n")
      }
    }

    printf("\n Finished testing. \n %d of %d tests passed.", passedCount, TEST_COUNT);
    return 0;
}

/* Check that a null pointer is returned if no space is requested */
bool test_00() {
  void *p = mem_alloc(0);
  return (p == NULL);
}

/* Ensure that the allocator successfully returns null if memory is
 * exhausted and there is no free space on the heap. */
bool test_01() {
  memory_exhausted = TRUE;
  void *p = mem_alloc(0xF0000);
  return (p == NULL);
}

/* Simple allocation of a single integer, and checking that the 
 * value is read correctly. */
bool test_02() {
    int *x = mem_alloc(sizeof(int));
    *x = 3;
    return (*x == 3);
}

/* Allocation of a chunk which is larger than the blocks provided
 * by the kernel */
bool test_03() {
  void *p = mem_alloc(MEM_BLOCK_SIZE + 1);
  return (p != NULL); // If no segfault; assume success
}

/* Create one node for each bin size */
bool test_04() {
  for (int i = 0; i < BIN_COUNT; i++) {
    void *p = mem_alloc(1 << i);
    if (p == NULL) return false;
  }

  return true;
}

/* Allocate many blocks of the same size */
bool test_05() {
  for (int i = 0; i < 0xF0000; i++) {
    void *p = mem_alloc(1);
    if (p == NULL) return false;
  }
  
  return true;
}

/* Attempt a simple call to free */
bool test_06() {
  void *p = mem_alloc(1);
  free(p);
  return true; // If no segfault; assume success
}

/* Free a chunk which is larger than a block provided by the kernel */
bool test_07() {
  void *p = mem_alloc(MEM_BLOCK_SIZE + 1);
  free(p);
  return true;
}

/* Repeatedly allocate and free blocks to test against memory leaks */
bool test_07() {
  for (int i = 0; i < 0xF000; i++) {
    void *p = mem_alloc(0xF000);
    if (p == NULL && i > 0) return false; // Memory leak
    mem_free(p);
  }

  return true;
}

// Simpply calls one of the tests, according to the test number
bool test(int testNumber) {
  switch (testNumber) {
    case  0: return test_00();
    case  1: return test_01();
    case  2: return test_02();
    case  3: return test_03();
    case  4: return test_04();
    case  5: return test_05();
    case  6: return test_06();
    case  7: return test_07();
  }
}
