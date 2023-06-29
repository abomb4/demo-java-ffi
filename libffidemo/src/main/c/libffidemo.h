
#ifndef _LIBFFIDEMO_H_
#define _LIBFFIDEMO_H_

int demo_sum(int, int);

/* 返回字符串，调用方需要自行回收内存 */
char *demo_generate_hello(const char *);

typedef struct {
    int id;
    char *name;
    double score;
} complex_t;

/* 返回结构体的副本，调用方需要自行回收内存 */
complex_t* get_by_id(int);

/* 返回结构体的副本的数组，调用方需要自行回收内存 */
complex_t** get_by_name_like(const char *);

#endif
