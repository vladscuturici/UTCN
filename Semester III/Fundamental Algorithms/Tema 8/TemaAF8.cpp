#include <iostream>
using namespace std;

struct Node {
    int key;
    struct Node* left, * right;
};

Node* s[200];
Node* p;
int cnt = 0;

void push(Node* x)
{
    s[cnt] = x;
    cnt++;
}

void pop()
{
    p = s[cnt-1];
    s[cnt-1] = NULL;
    cnt--;
}

Node* addNode(int key)
{
    Node* x = new Node;
    x->key = key;
    x->left = NULL;
    x->right = NULL;
    return x;
}


void print_preorder_rec(struct Node* x)
{
    if (x == NULL)
        return;
    cout << x->key << " ";
    print_preorder_rec(x->left);
    print_preorder_rec(x->right);
}

void print_preorder_ite(struct Node* x)
{
    push(x);
    while (s[0])
    {
        pop();
        cout << p->key << " ";
        if (p->right)
            push(p->right);
        if (p->left)
            push(p->left);
    }
}

void pretty_print(Node* x, int level)
{
    if (x != NULL)
    {
        pretty_print(x->right, level + 1);
        for (int i = 1; i <= level; i++)
            printf("     ");
        cout << x->key;
        cout << "\n";
        pretty_print(x->left, level + 1);
    }
}

void insertsort(int v[], int p, int n)
{
    int i, x, j;
    for (i = p + 1; i < n + 1; i++)
    {
        x = v[i];
        j = i;
        while (j > p && v[j - 1] > x)
        {
            v[j--] = v[j - 1];
        }
        v[j] = x;
    }
}


int partition(int v[], int t, int r)
{
    int q = v[r];
    int i = t - 1;
    for (int j = t; j < r; j++)
    {
        if (v[j] <= q)
        {
            i++;
            swap(v[i], v[j]);
        }
    }
    swap(v[i + 1], v[r]);
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

void hquicksort(int v[], int p, int r)
{
    int q;
    while (p < r)
    {
        if (r - p < 30)
        {
            insertsort(v, p, r);
            return;
        }
        q = partition(v, p, r);
        if (q - p < r - q)
        {
            hquicksort(v, p, q - 1);
            p = q + 1;
        }
        else
        {
            hquicksort(v, q + 1, r);
            r = q - 1;
        }
    }
}

void print(int v[], int n)
{
    for (int i = 0; i < n; ++i)
        cout << v[i] << " ";
    cout << "\n";
}

void demo_parcurgere()
{
    Node* Tree[20];
    Tree[1] = addNode(2);
    Tree[2] = addNode(1);
    Tree[1]->left = Tree[2];
    Tree[3] = addNode(4);
    Tree[1]->right = Tree[3];
    Tree[4] = addNode(3);
    Tree[3]->left = Tree[4];
    Tree[5] = addNode(5);
    Tree[3]->right = Tree[5];
    Tree[6] = addNode(6);
    Tree[5]->right = Tree[6];
    pretty_print(Tree[1], 0);
    cout << "Recursiv\n";
    print_preorder_rec(Tree[1]);
    cout << "\nIterativ\n";
    print_preorder_ite(Tree[1]);
}

void demo_hquicksort()
{
    int v1[] = { 4, 2, 1, 5, 0, 6, 3 };
    int n1 = sizeof(v1) / sizeof(v1[0]);
    int v2[] = { 4, 2, 1, 5, 0, 6, 3 };
    int n2 = sizeof(v2) / sizeof(v2[0]);
    int v3[] = { 44, 1122, 21, 65, 90, 7436, 3, 2, 53, 12, 72, 9823, 215, 5432, 912, 16913, 4237, 563412, 613, 323, 9324, 80, 9632, 682, 6518, 124, 581, 69012, 5, 128, 578,  21, 6904, 2, 49, 592, 2184, 12034, 2381230, 1239, 684, 3862, 968};
    int n3 = sizeof(v3) / sizeof(v3[0]);
    quicksort(v1, 0, n1 - 1);
    insertsort(v2, 0, n2 - 1);
    hquicksort(v3, 0, n3 - 1);
    cout << "\nQuicksort\n";
    print(v1, 7);
    cout << "Insertion sort\n";
    print(v2, 7);
    cout << "Hybrid Quicksort\n";
    print(v3, n3);
}

int main()
{
    demo_parcurgere();
    demo_hquicksort();
    return 0;
}