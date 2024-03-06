#include <iostream>
#include "Profiler.h"

using namespace std;

Profiler p("sort");

#define maxsize 10000
#define stepsize 100
#define nr_tests 5
void insertsort(int v[], int n)
{
    Operation opComp = p.createOperation("insertComp", n);
    Operation opAttr = p.createOperation("insertAttr", n);
    int i, j, k;
    for (i = 1; i < n; i++)
    {
        k = v[i];
        opAttr.count();
        j = i - 1;
        while (j >= 0 && v[j] > k)
        {
            opComp.count();
            v[j + 1] = v[j];
            opAttr.count(); 
            j--;
        }
        if (j >= 0)
            opComp.count();
        v[j + 1] = k;
        opAttr.count();
    }
}

void selectsort(int v[], int n)
{
    Operation opComp = p.createOperation("selectComp", n);
    Operation opAttr = p.createOperation("selectAttr", n);
    int i, j, minpoz;
    for (i = 0; i < n - 1; i++)
    {
        j = i + 1;
        minpoz = i;
        while (j < n)
        {
            opComp.count();
            if (v[j] < v[minpoz])
                minpoz = j;
            j++;
        }
        if (minpoz != i)
        {
            swap(v[minpoz], v[i]);
            opAttr.count();
            opAttr.count();
            opAttr.count();
        }
    }
}

void bubblesort(int v[], int n)
{
    Operation opComp = p.createOperation("bubbleComp", n);
    Operation opAttr = p.createOperation("bubbleAttr", n);
    int i,j;
    for (i = 0; i < n - 1; i++)
    {
        j = 0;
        while (j < n - i - 1)
        {
            opComp.count();
            if (v[j] > v[j + 1])
            {
                swap(v[j], v[j + 1]);
                opAttr.count();
                opAttr.count();
                opAttr.count();
            }
            j++;
        }
    }
}

void print(int v[], int n)
{
    for (int i = 0; i < n; i++)
    {
        cout << v[i] << " ";
    }
}

void demo()
{
    int v1[] = { 15, 1, 4, 21, 23, 213, 12234, 5981, 2132193, 38128712, 2108321938, 0, 1242 }, v2[100], v3[100];
    int n = sizeof(v1) / sizeof(v1[0]);
    copy(v1, v1 + n, v2);
    copy(v1, v1 + n, v3);
    insertsort(v1, n);
    cout << "Insertion sort\n";
    print(v1, n);
    cout << "\n";
    selectsort(v2, n);
    cout << "Selection sort\n";
    print(v2, n);
    cout << "\n";
    bubblesort(v3, n);
    cout << "Bubble sort\n";
    print(v3, n);
    cout << "\n";
}

void perf(int order)
{
    int v1[maxsize], v2[maxsize], v3[maxsize];
    int n;
    for (n = stepsize; n <= maxsize; n += stepsize)
    {
        for (int test = 1; test <= nr_tests; test++)
        {
            FillRandomArray(v1, n, 5, 50000, false, order);
            copy(v1, v1 + maxsize, v2);
            copy(v1, v1 + maxsize, v3);
            insertsort(v1, n);
            selectsort(v2, n);
            bubblesort(v3, n);
        }
    }
    p.divideValues("insertAttr", nr_tests);
    p.divideValues("insertComp", nr_tests);
    p.addSeries("insertTotalAttrComp", "insertAttr", "insertComp");
    p.divideValues("selectAttr", nr_tests);
    p.divideValues("selectComp", nr_tests); 
    p.addSeries("selectTotalAttrComp", "selectAttr", "selectComp");
    p.divideValues("bubbleAttr", nr_tests);
    p.divideValues("bubbleComp", nr_tests);
    p.addSeries("bubbleTotalAttrComp", "bubbleAttr", "bubbleComp");
}

void perf_all()
{
    perf(UNSORTED);
    p.reset("best");
    perf(ASCENDING);
    p.reset("worst");
    perf(DESCENDING);
    p.showReport();
}

int main()
{
    //demo();
    perf_all();
    return 0;
}
//CAZUL MEDIU STATISTIC - observam ca cea mai eficienta metoda este insert, urmata de select la o diferenta nu foarte mare iar bubblesort este cu mult 
//mai ineficienta decat celelalte 2 metode.
//WORST CASE - in acest caz observam ca cea mai eficienta metoda este selection sort, urmata de insertion sort, iar la final bubble sort, diferenta fiind
//foarte mare intre ele
//BEST CASE - la cazul cel mai favorabil, insertion sort este de departe cel mai eficient, iar bubble sort si selection sort sunt comparabile in eficienta
//in concluzie, selection sortul este mai bun pe un set de date in care se apropie de cazul cel mai nefavorabil, iar insertion sortul este mai eficient
//cand vine vorba de date apropiate de cel mai favorabil caz, bubble sortul fiind cel mai ineficient in majoritatea cazurilor