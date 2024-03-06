#include <iostream>
using namespace std;

struct Node
{
	struct Node* parent;
	int key;
	int rank;
};

Node* addNode(int key)
{
	Node* x = new Node;
	x->key = key;
	x->parent = NULL;
	x->rank = 1;
	return x;
}

void make_set(Node* x)
{
	x->rank = 1;
	x->parent = x;
}

void link(Node* x, Node* y)
{
	if (x->rank <= y->rank)
		x->parent = y;
	else 
		y->parent = x;
	if (x->rank == y->rank)
		(y->rank)++;
}

Node* find_set(Node* x)
{
	if (x->parent == x)
		return x->parent;
	x->parent = find_set(x->parent);
}

void union_set(Node* x, Node* y)
{
	link(find_set(x), find_set(y));
}

int main()
{
	Node* v[20];
	for (int i = 1; i <= 20; i++)
	{
		v[i] = addNode(rand() % 100 + 1);
		make_set(v[i]);
	}
	for (int i = 1; i <= 20; i++)
	{
		union_set(v[rand() % 20], v[i]);
	}
	for (int i = 1; i <= 20; i++)
	{
		cout << v[i]->key << " cu rank - " << v[i]->rank << " si parent " << v[i]->parent->key << "\n";
	}
	return 0;
}