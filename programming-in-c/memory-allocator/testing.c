#include <assert.h>
#include <stdbool.h>

// ----------------------------------------------
//   TESTING
// ----------------------------------------------
extern bool memory_exhausted;

#define TEST_COUNT 10

int main() {
    printf("Initialising memory");

    // Initialise the memory
    mem_init();

    printf("Memory has been initialised... \nRunning all tests:");
    int passedCount = 0; // Running total of the number of passes

    // Run all of the tests
    for (int i = 1; i < TEST_COUNT; i++) {
      printf("Running test #%d...", i);
      bool result = test(i);
    }

    printf("")
    test_1();
    return 0;
}

/* Simple allocation of a single integer, and checking that the 
 * value is read correctly. */
bool test_01() {
    int *x = mem_alloc(sizeof(int));
    *x = 3;
    return (*x == 3);
}

bool test02() {

}

bool test3() {

}

bool test4() {

}

// Simpply calls one of the tests, according to the test number
bool test(int testNumber) {
  switch (testNumber) {
    case  1: return test_01();
    case  2: return test_02();
    case  3: return test_03();
    case  4: return test_04();
    case  5: return test_05();
    case  6: return test_06();
    case  7: return test_07();
    case  8: return test_08();
    case  9: return test_09();
    case 10: return test_10();
    case 11: return test_11();
  }
}
