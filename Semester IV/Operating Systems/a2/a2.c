#include "a2_helper.h"
#include <sys/wait.h>
#include <semaphore.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>

pid_t pid2, pid3, pid4, pid5, pid6, pid7;
pthread_t t[5], t6[43], t3_arr[6];
pthread_mutex_t mutex_t2, mutex_t2, mutex_p3;
pthread_cond_t cond_t4, cond_t2, cond_p3, cond_t6;
sem_t sem_p6, sem_t6;
int p3_cnt, cnt;

void *t1(void *arg) {
    //printf("thread1");
    info(BEGIN, 2, 1);
    info(END, 2, 1);
    return NULL;
}

void *t2(void *arg) {
    //printf("thread2");
    info(BEGIN, 2, 2);
    pthread_mutex_lock(&mutex_t2);
    pthread_cond_signal(&cond_t2); 
    pthread_cond_wait(&cond_t4, &mutex_t2);
    pthread_mutex_unlock(&mutex_t2);
    info(END, 2, 2);
    return NULL;
}

void *t3(void *arg) {
    //printf("thread3");
    info(BEGIN, 2, 3);
    info(END, 2, 3);
    return NULL;
}

void *t4(void *arg) {
    //printf("thread4");
    pthread_mutex_lock(&mutex_t2);
    pthread_cond_wait(&cond_t2, &mutex_t2); 
    pthread_mutex_unlock(&mutex_t2);
    info(BEGIN, 2, 4);
    info(END, 2, 4);
    pthread_cond_signal(&cond_t4);
    return NULL;
}

void *t5(void *arg) {
    //printf("thread5");
    info(BEGIN, 2, 5);
    info(END, 2, 5);
    return NULL;
}

void create_threads2() {
    pthread_mutex_init(&mutex_t2, NULL);
    pthread_cond_init(&cond_t4, NULL);
    pthread_create(&t[0], NULL, t1, NULL);
    pthread_create(&t[1], NULL, t2, NULL);
    pthread_create(&t[2], NULL, t3, NULL);
    pthread_create(&t[3], NULL, t4, NULL);
    pthread_create(&t[4], NULL, t5, NULL);
    for (int i = 0; i < 5; i++) 
        pthread_join(t[i], NULL);
    pthread_cond_destroy(&cond_t4);
    pthread_mutex_destroy(&mutex_t2);
}

void *t6_6(void *arg) {
    cnt++;
    //printf("%d", cnt);
    sem_wait(&sem_p6);
    int n = *((int *)arg);
    pthread_mutex_lock(&mutex_t2);
    if (n == 11) 
        while (cnt != 5) 
            pthread_cond_wait(&cond_t6, &mutex_t2);
    else 
        pthread_cond_signal(&cond_t6);
    pthread_cond_destroy(&cond_t6);
    pthread_mutex_unlock(&mutex_t2);
    info(BEGIN, 6, n);
    info(END, 6, n);
    pthread_mutex_lock(&mutex_t2);
    cnt--;
    pthread_mutex_unlock(&mutex_t2);
    if (n == 11) 
        sem_post(&sem_t6);
    sem_post(&sem_p6);
    free(arg);
    return NULL;
}

void *t3_3(void *arg) {
    p3_cnt++;
    int id = *((int *)arg);
    info(BEGIN, 3, id);
    info(END, 3, id);
    pthread_mutex_lock(&mutex_p3);
    pthread_cond_signal(&cond_p3);
    pthread_mutex_unlock(&mutex_p3);
    free(arg);
    return NULL;
}

void create_threads6() {
    sem_init(&sem_p6, 0, 5);
    sem_init(&sem_t6, 0, 0);
    for (int i = 0; i < 43; i++) {
        int *n = malloc(sizeof(int));
        *n = i + 1;
        pthread_create(&t6[i], NULL, t6_6, n);
    }
    for (int i = 0; i < 43; i++) {
        if (i == 10) 
            sem_wait(&sem_t6);
        pthread_join(t6[i], NULL);
    }
    sem_destroy(&sem_t6);
    sem_destroy(&sem_p6);
}

void create_threads3() {
    pthread_cond_init(&cond_p3, NULL);
    pthread_mutex_init(&mutex_p3, NULL);
    for (int i = 0; i < 6; i++) {
        //printf("%d", n);
        int *n = malloc(sizeof(int));
        *n = i + 1;
        pthread_create(&t3_arr[i], NULL, t3_3, n);
    }
    pthread_mutex_lock(&mutex_p3);
    while (p3_cnt != 6) 
        pthread_cond_wait(&cond_p3, &mutex_p3);
    pthread_cond_destroy(&cond_p3);
    pthread_mutex_unlock(&mutex_p3);
    for (int i = 0; i < 6; i++) 
        pthread_join(t3_arr[i], NULL);
    pthread_mutex_destroy(&mutex_p3);
}

void process_tree(void) {
    pid2 = fork();
    if (pid2 == 0) {
        info(BEGIN, 2, 0);
        pid3 = fork();
        //printf("%d", pid3);
        if (pid3 == 0) {
            info(BEGIN, 3, 0);
            create_threads3();
            info(END, 3, 0);
            exit(0);
        } else 
            waitpid(pid3, NULL, 0);
        pid5 = fork();
        //printf("%d", pid5);
        if (pid5 == 0) {
            info(BEGIN, 5, 0);
            info(END, 5, 0);
            exit(0);
        } else 
            waitpid(pid5, NULL, 0);
        pid6 = fork();
        //printf("%d", pid6);
        if (pid6 == 0) {
            info(BEGIN, 6, 0);
            create_threads6();
            info(END, 6, 0);
            exit(0);
        } else 
            waitpid(pid6, NULL, 0);
        pid7 = fork();
        if (pid7 == 0) {
            info(BEGIN, 7, 0);
            info(END, 7, 0);
            exit(0);
        } else 
            waitpid(pid7, NULL, 0);
        create_threads2();
        info(END, 2, 0);
        exit(0);
    } else 
        waitpid(pid2, NULL, 0);
    pid4 = fork();
    if (pid4 == 0) {
        info(BEGIN, 4, 0);
        info(END, 4, 0);
        exit(0);
    } 
    //printf("%d", pid4);
    else 
        waitpid(pid4, NULL, 0);
    info(END, 1, 0);
}

int main(){
    init();
    info(BEGIN, 1, 0);
    //printf("%d", pid1);
    process_tree();
    //info(END, 1, 0);
    return 0;
}
