#include <iostream>
#include "Profiler.h"

using namespace std;

Profiler p("quicksort");

#define maxsize 10000
#define stepsize 100
#define nr_tests 5

int length;

void heapify(int v[], int H, int i)
{
    int largest, left, right;
    largest = i;
    left = 2 * i + 1;
    right = 2 * i + 2;
    if (left < H)
    {
        p.countOperation("hsOp", length);
        if (v[left] > v[largest])
            largest = left;
    }
    if (right < H)
    {
        p.countOperation("hsOp", length);
        if (v[right] > v[largest])
            largest = right;
    }
    if (largest != i) {
        swap(v[i], v[largest]);
        p.countOperation("hsOp", length);
        p.countOperation("hsOp", length);
        p.countOperation("hsOp", length);
        heapify(v, H, largest);
    }
}

void build_heap_bottom_up(int v[], int H)
{
    int i, j;
    j = (H / 2) - 1;
    for (i = j; i >= 0; i--)
        heapify(v, H, i);
}

void heapsort(int v[], int n)
{
    build_heap_bottom_up(v, n);
    for (int i = n - 1; i > 0; i--)
    {
        swap(v[0], v[i]);
        p.countOperation("hsOp", n);
        p.countOperation("hsOp", n);
        p.countOperation("hsOp", n);
        n--;
        heapify(v, n, 0);
    }
}

int partition(int v[], int t, int r) 
{
    int q = v[r];
    p.countOperation("qsOp", length);
    int i = t - 1;
    for (int j = t; j < r; j++) 
    {
        p.countOperation("qsOp", length);
        if (v[j] <= q) 
        {
            i++;
            swap(v[i], v[j]);
            p.countOperation("qsOp", length);
            p.countOperation("qsOp", length);
            p.countOperation("qsOp", length);
        }
    }
    swap(v[i + 1], v[r]);
    p.countOperation("qsOp", length);
    p.countOperation("qsOp", length);
    p.countOperation("qsOp", length);
    return i + 1;
}

void quicksort(int v[], int p, int r)
{
    int q;
    if (p < r)
    {
        q = partition(v, p, r);
        quicksort(v, p, q - 1);
        quicksort(v, q + 1, r);
    }
}

int partition_r(int v[], int p, int r)
{
    int k = p + rand() % (r - p);
    swap(v[k], v[r]);
    return partition(v, p, r);
}

void rquicksort(int v[], int p, int r)
{
    if (p < r) {
        int q = partition_r(v, p, r);
        rquicksort(v, p, q - 1);
        rquicksort(v, q + 1, r);
    }
}

void it_bubblesort(int v[], int n)
{
    Operation ops = p.createOperation("ibsOp", n);
    int i, j, k;
    for (i = 0; i < n - 1; i++)
    {
        j = 0;
        k = 0;
        while (j < n - i - 1)
        {
            ops.count();
            if (v[j] > v[j + 1])
            {
                swap(v[j], v[j + 1]);
                ops.count();
                ops.count();
                ops.count();
                k = 1;
            }
            j++;
        }
        if (k == 0)
            break;
    }
}

void rec_bubblesort(int v[], int n)
{
    if (n == 1)
        return;
    int k = 0;
    for (int i = 0; i < n - 1; i++)
    {
        p.countOperation("rbsOp", length);
        if (v[i] > v[i + 1])
        {
            swap(v[i], v[i + 1]);
            p.countOperation("rbsOp", length);
            p.countOperation("rbsOp", length);
            p.countOperation("rbsOp", length);
            k++;
        }
    }
    if (k == 0)
        return;
    rec_bubblesort(v, n - 1);
}

void print(int v[], int n)
{
    for (int i = 0; i < n; ++i)
        cout << v[i] << " ";
    cout << "\n";
}

void demo()
{
    int v1[] = { 15, 14, 12, 10, 9, 4, 2, 1 }, v2[] = { 15, 14, 12, 10, 9, 4, 2, 1 }, v3[] = { 15, 14, 12, 10, 9, 4, 2, 1 };
    int v4[] = { 15, 14, 12, 10, 9, 4, 2, 1 }, v5[] = { 15, 14, 12, 10, 9, 4, 2, 1 };
    int n1 = sizeof(v1) / sizeof(v1[0]);
    int n2 = sizeof(v2) / sizeof(v2[0]);
    int n3 = sizeof(v3) / sizeof(v3[0]);
    int n4 = sizeof(v4) / sizeof(v4[0]);
    int n5 = sizeof(v5) / sizeof(v5[0]);
    cout << "Heapsort\n";
    heapsort(v1, n1);
    print(v1, n1);
    cout << "Quicksort\n";
    quicksort(v2, 0, n2 - 1);
    print(v2, n2);
    cout << "Bubblesort iterativ\n";
    it_bubblesort(v3, n3);
    print(v3, n3);
    cout << "Bubblesort recursiv\n";
    rec_bubblesort(v4, n4);
    print(v4, n4);
    cout << "Randomized - select quicksort\n";
    rquicksort(v5, 0, n5 - 1);
    print(v5, n5);
    cout << '\n';
}

void perf_quicksort(int order)
{
    int v[maxsize];
    int n = maxsize;
    for (n = stepsize; n <= maxsize; n += stepsize)
    {
        length = n;
        for (int test = 1; test <= nr_tests; test++)
        {
            FillRandomArray(v, n, 5, 50000, false, order);
            length = n;
            quicksort(v, 0, n-1);
        }
    }
    p.divideValues("qsOp", nr_tests);
}

void perf_quicksort_awb()
{
    perf_quicksort(UNSORTED);
    p.reset("best");
    perf_quicksort(ASCENDING);
    p.reset("worst");
    perf_quicksort(DESCENDING);
    p.showReport();
}

void perf()
{
    int v[maxsize];
    int n=maxsize;
    for (n = stepsize; n <= maxsize; n += stepsize)
    {
        length = n;
        for (int test = 1; test <= nr_tests; test++)
        {
            FillRandomArray(v, n);
            length = n;
            quicksort(v, 0, n-1);
        }
    }
    p.divideValues("qsOp", nr_tests);
    p.showReport();
}

int main()
{
    //demo();
    perf();
}
//Scuturici Vlad Lucian - Grupa 30225