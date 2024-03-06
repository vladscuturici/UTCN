#include <iostream>
#include "Profiler.h"

using namespace std;

Profiler p("sort");

#define maxsize 10000
#define stepsize 100
#define nr_tests 5

void heapify(int v[], int H, int i)
{
    Operation opComp = p.createOperation("heapifyComp", H);
    Operation opAttr = p.createOperation("heapifyAttr", H);
    int largest, left, right;
    largest = i;
    left = 2 * i + 1;
    right = 2 * i + 2;
    if (left < H && v[left] > v[largest])
        largest = left;
    opComp.count();
    if (right < H && v[right] > v[largest])
        largest = right;
    opComp.count();
    if (largest != i) {
        swap(v[i], v[largest]);
        opAttr.count();
        opAttr.count();
        opAttr.count();
        heapify(v, H, largest);
    }
}

void build_heap_bottom_up(int v[], int H)
{
    int i, j;
    j = (H / 2) - 1;
    for (i = j; i >= 0; i--) 
    {
        heapify(v, H, i);
    }
}

void heapsort(int v[], int n)
{
    Operation opComp = p.createOperation("heapsortComp", n);
    Operation opAttr = p.createOperation("heapsortAttr", n);
    build_heap_bottom_up(v, n);
    for (int i = n-1; i > 0; i--)
    {
        swap(v[0], v[i]);
        opAttr.count();
        opAttr.count();
        opAttr.count();
        n--;
        heapify(v, n, 0);
    }
}

void print(int v[], int n)
{
    for (int i = 0; i < n; ++i)
        cout << v[i] << " ";
    cout << "\n";
}

void perf(void)
{
    int v1[maxsize], v2[maxsize];
    int n;
    for (n = stepsize; n <= maxsize; n += stepsize)
    {
        for (int test = 1; test <= nr_tests; test++)
        {
            FillRandomArray(v1, n);
            FillRandomArray(v2, n);
            build_heap_bottom_up(v1, n);
            heapsort(v2, n);
        }
    }
    p.divideValues("heapifyAttr", nr_tests);
    p.divideValues("heapifyComp", nr_tests);
    p.addSeries("heapifyTotalAttrComp", "heapifyAttr", "heapifyComp");
    p.divideValues("heapsortAttr", nr_tests);
    p.divideValues("heapsortComp", nr_tests);
    p.addSeries("heapsortTotalAttrComp", "heapsortAttr", "heapsortComp");
}

void demo()
{
    int v1[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, v2[] = {15, 14, 12, 10, 9, 7, 5, 4, 2, 1};
    int n = sizeof(v1) / sizeof(v1[0]);
    int nhs = sizeof(v2) / sizeof(v2[0]);
    build_heap_bottom_up(v1, n);
    cout << "Bottom - up\n";
    print(v1, n);
    cout << '\n';
    heapsort(v2, nhs);
    cout << "Heapsort\n";
    print(v2, nhs);
    cout << '\n';
}

int main()
{
    //demo();
    perf();
    return 0;
}
//Scuturici Vlad Lucian - grupa 30225