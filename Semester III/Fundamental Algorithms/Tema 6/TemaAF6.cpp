#include <iostream>
using namespace std;

struct node{
    int key;
    int ch[20];
}R2[200];

int cnt = 0, lvl[200];
int M[500][500];

void find_ch(int v[], int n, int r, int l)
{
    int ch[50], k = 0;
    for (int i = 1; i <= n; i++)
        if (v[i] == r)
        {
            k++;
            ch[k] = i;
        }
    if (k == 0)
        return;
    for (int i = 1; i <= k; i++)
        M[r][i] = ch[i];
    for (int i = 1; i <= k; i++)
        find_ch(v, n, ch[i], l + 1);
}

void find_level(int v[], int n, int level, int f)
{
    int i = 1, k = 0;
    for (int i = 1; i <= n; i++)
        if (v[i] == f)
        {
            lvl[i] = level;
            k++;
            find_level(v, n, level + 1, i);
        }
    if (k == 0)
        return;
}

void print2(int r)
{
    int i = 1;
    if (lvl[r] != 0)
        cout << "->";
    cout << r;
    while (M[r][i] != 0)
    {
        print2(M[r][i]);
        i++;
    }
    cout << "\n";
    for (int i = 1; i <= lvl[r]; i++) 
        cout << "  ";
    if (r > 9)
        cout << " ";
}

void pretty_print_R1(int v[], int n)
{
    cnt = 0;
    int r, i=1;
    while (v[i] != -1)
        i++;
    r = i;
    cnt++;
    find_level(v, n, 1, r);
    find_ch(v, n, r, 1);
    print2(r);
}

void print(int v[], int n)
{
    for (int i = 1; i <= n; i++)
        cout << v[i] << " ";
    cout << "\n";
}

void T2(int v[], int n)
{
    int k;
    for (int i = 1; i <= n; i++)
    {
        R2[i].key = i;
        k = 1;
        while (M[i][k] != 0)
        {
            R2[i].ch[k-1] = M[i][k];
            k++;
        }
    }
}

void verif_T2(int n)
{
    int k;
    for (int i = 1; i <= n; i++)
    {
        cout << R2[i].key << "\n";
        k = 0;
        while (R2[i].ch[k] != 0) {
            cout << R2[i].ch[k] << " ";
            k++;
        }
        cout << "\n";
    }
}

int main()
{
    int v[] = { 0, 7, 3, 5, 3, -1, 3, 5};
    int n = 7, k;
    print(v, 7);
    pretty_print_R1(v, n);
    T2(v, n);
    verif_T2(n);
    return 0;
}
//Scuturici Vlad Lucian - 30225

