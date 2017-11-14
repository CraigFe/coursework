all: 
	gcc -Wall -pedantic -c kernel.c
	gcc -Wall -pedantic -c allocator.c
	gcc -Wall -pedantic -c linkedlist.c
	gcc -Wall -pedantic -c testing.c
	gcc -o testing kernel.o allocator.o linkedlist.o testing.o

clean:
	rm -f kernel.o linkedlist.o allocator.o testing.o testing
