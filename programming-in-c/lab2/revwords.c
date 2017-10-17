#include <ctype.h>
#include <string.h>
<<<<<<< Updated upstream
#include "revwords.h"

void reverse_substring(char str[], int start, int end) { 
  /* TODO */
}


int find_next_start(char str[], int len, int i) { 
  /* TODO */
  return 0;
}

int find_next_end(char str[], int len, int i) {
  /* TODO */
  return 0;
}

void reverse_words(char s[]) { 
  /* TODO */
}
=======
#include <stdio.h>
#include "revwords.h"

void reverse_words(char* s) {
  char* word_begin = s;
  char* temp = s;

  while (*temp++) {
    if (*temp == '\0')
      reverse_substring(word_begin, (char*) temp-1);

    else if (*temp == ' ') {
      reverse_substring(word_begin, (char*) temp-1)
      word_begin = (char*) temp + 1;
    }
  }
}

void reverse_substring(char* begin, char* end) {
  char tmp;
  while (begin < end) {
    tmp = *begin;
    *begin++ = *end;
    *end-- = temp;
  }
}

// void reverse_words(char s[]) { 
//   int i = 0, 
//     j = 0,
//     len = strlen(s) - 1;

//   while (i < len) {
//     i = find_next_start(s, len, i);
//     j = find_next_end(s, len, i);
//     if (j == -1) return;
//     reverse_substring(s, i, j);
//     i = j + 1;
//   }
// }
>>>>>>> Stashed changes
