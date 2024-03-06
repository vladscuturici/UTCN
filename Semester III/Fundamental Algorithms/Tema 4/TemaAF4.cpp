#include <iostream>
#include "Profiler.h";
using namespace std;

Profiler p("MergeK");

struct Node 
{
    int data;
    struct Node* next;
    struct Node* prev;
};

struct Node* klist[200];
struct Node* w;
struct Node* z;
int v[200];
int heapsize;

void print(int v[], int n)
{
    for (int i = 0; i < n; ++i)
        cout << v[i] << " ";
    cout << "\n";
}

void insert_first(struct Node** head, int x)
{
    struct Node* new_node = new Node;
    new_node->next = (*head);
    new_node->prev = NULL;
    new_node->data = x;
    if ((*head) != NULL)
        (*head)->prev = new_node;
    (*head) = new_node;
}

void insert_after(struct Node* prev_node, int x)
{
    if (prev_node == NULL) 
        return;
    struct Node* new_node = new Node;
    new_node->data = x;
    new_node->next = prev_node->next;
    prev_node->next = new_node;
    new_node->prev = prev_node;
    if (new_node->next != NULL)
        new_node->next->prev = new_node;
}

void insert_last(struct Node** head, int x)
{
    struct Node* new_node = new Node;
    struct Node* last = *head;
    new_node->data = x;
    new_node->next = NULL;
    if (*head == NULL) {
        new_node->prev = NULL;
        *head = new_node;
        return;
    }
    while (last->next != NULL)
        last = last->next;
    last->next = new_node;
    new_node->prev = last;
    return;
}

void print_list(struct Node* node) 
{
    while (node != NULL) {
        cout << node->data << " ";
        node = node->next;
    }
    cout << "\n";
    
}

void generate(int k, int n)
{
    int j, x[200];
    struct Node* head = NULL;
    for (int i = 0; i < n; i++)
    {
        j = rand() % k;
        v[rand() % k]++;
    }
    cout << "Dimensiunea pentru fiecare lista:\n";
    print(v, k);
    for (int i = 0; i < n; i++)
    {
        klist[i] = NULL;
        FillRandomArray(x, v[i], 5, 50000, false, 1);
        for (int l = 0; l < v[i]; l++)
        {
            insert_last(&klist[i], x[l]);
        }
    }
}

void merge_2(struct Node* l1, struct Node* l2, int n1, int n2)
{
    w = NULL;
    int i, j;
    i = 0;
    j = 0;
    while (i < n1 && j < n2)
    {
        if (l1->data < l2->data)
        {
            insert_last(&w, l1->data);
            l1 = l1->next;
            i++;
        }
        else
        {
            insert_last(&w, l2->data);
            l2 = l2->next;
            j++;
        }
    }
    while (i < n1)
    {
        insert_last(&w, l1->data);
        l1 = l1->next;
        i++;
    }
    while (j < n2)
    {
        insert_last(&w, l2->data);
        l2 = l2->next;
        j++;
    }
}

void demo()
{
    int k = 4, n = 20;
    srand(time(0));
    cout << k << " liste cu in total " << n << " elemente\n";
    cout << "\n";
    generate(k, n);
    for (int i = 0; i < k; i++)
    {
        cout << "Lista nr. " << i + 1 << ": ";
        print_list(klist[i]);
    }
    cout << "\n";
    cout << "Interclasarea primelor 2 liste:\n";
    merge_2(klist[0], klist[1], v[0], v[1]);
    print_list(w);
    cout << "\n";
}

int main() 
{
    demo();
    return 0;
}
//Scuturici Vlad Lucian - grupa 30225