#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <stdbool.h>
#include "libffidemo.h"

// List of names
const char* names[] = {
    "John", "张彻", "Bob", "Emma", "张航发",
    "李斯", "张三", "王五", "William", "张湾所",
    "David", "刘占", "Daniel", "莫迪分", "Benjamin",
    "柴欧", "杨家", "Emily", "Ethan", "Harper"
};

const char* greetings[] = {
    "Hello", "Bonjour", "Hola", "Ciao", "你好"
};
const int num_greetings = sizeof(greetings) / sizeof(greetings[0]);

bool inited = false;
complex_t data[20];

void initialize_data() {
    srand(time(NULL));
    int num_names = sizeof(names) / sizeof(names[0]);
    for (int i = 0; i < 20; i++) {
        data[i].id = i + 1;
        data[i].name = strdup(names[i % num_names]);
        data[i].score = 50.0 + ((double)rand() / RAND_MAX) * 50.0;
    }
}

int demo_sum(int a, int b) {
    return a + b;
}

char* demo_generate_hello(const char *name) {
    srand(time(NULL));

    // Randomly select a greeting
    int index = rand() % num_greetings;
    const char* greeting = greetings[index];

    // Calculate the length of the result string
    size_t name_len = strlen(name);
    size_t greeting_len = strlen(greeting);
    size_t result_len = greeting_len + name_len + 3; // 3: space, comma, null terminator

    // Allocate memory for the result string
    char* result = (char*)malloc((result_len + 1) * sizeof(char));
    if (result == NULL) {
        return NULL; // Memory allocation failed
    }

    // Construct the result string
    snprintf(result, result_len + 1, "%s, %s!", greeting, name);

    return result;
}

complex_t* get_by_id(int id) {
    if (!inited) {
        initialize_data();
        inited = true;
    }
    if (id < 1 || id > 20) {
        return NULL;
    }
    complex_t *entry = &data[id - 1];
    complex_t *copy = (complex_t*)malloc(sizeof(complex_t));
    if (copy == NULL) {
        return NULL;
    }

    // Copy the data to the new instance
    copy->id = entry->id;
    copy->name = strdup(entry->name);
    copy->score = entry->score;

    return copy;
}

complex_t** get_by_name_like(const char *name) {
    if (!inited) {
        initialize_data();
        inited = true;
    }
    int i, count = 0;
    complex_t **result = (complex_t**)malloc(sizeof(complex_t*) * 20);
    if (result == NULL) {
        return NULL;
    }

    for (i = 0; i < 20; i++) {
        if (strstr(data[i].name, name) != NULL) {
            complex_t *entry = &data[i];
            complex_t *copy = (complex_t*)malloc(sizeof(complex_t));
            if (copy != NULL) {
                // Copy the data to the new instance
                copy->id = entry->id;
                copy->name = strdup(entry->name);
                copy->score = entry->score;

                result[count++] = copy;
            }
        }
    }

    result[count] = NULL;  // Null-terminate the result array
    return result;
}
