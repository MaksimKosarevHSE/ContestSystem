#include <vector>
#include <map>
#include <set>
#include <cmath>
#include <algorithm>
#include <functional>
#include <bitset>
#include <cstdio>
#include <climits>
#include <queue>
#include <string>
#include <unordered_set>
#include <sstream>
#include <forward_list>
#include <list>
#include <unordered_map>
#include <queue>
#include <iomanip>
#include <iostream>
#include <cstring>
#include <numeric>
#include <fstream>

using namespace std;
typedef long long ll;

template<typename T>
void print_v(std::vector<T> vec) {
    for_each(vec.begin(), vec.end(), [](T &el) { cout << el << " "; });
    cout << "\n";
}

template<typename T>
void print_v2d(std::vector<vector<T> > vec) { for_each(vec.begin(), vec.end(), [](vector<T> &row) { print_v(row); }); }

#define all(x) (x).begin(), (x).end()
#define pb push_back
#define int  long long
typedef pair<int, int> pair_i;

template<typename T>
istream &operator>>(istream &in, vector<T> &vec) {
    for (auto &el: vec) { in >> el; }
    return in;
}

template<typename T>
ostream &operator<<(ostream &out, vector<T> &vec) {
    for (auto &el: vec) out << el << " ";
    return out;
}

template<typename T, typename E>
ostream &operator<<(ostream &out, pair<T, E> &pr) {
    out << "{ " << pr.first << " : " << pr.second << " }";
    return out;
}

typedef vector<int> vi;
const long long MOD = 998244353;

ll binPow(ll x, ll p, ll m) {
    ll ans = 1;
    ll base = x % m;
    while (p > 0) {
        if (p % 2 == 1) ans = (ans * base) % m;
        base = (base * base) % m;
        p /= 2;
    }
    return ans % m;
}

#define INF LLONG_MAX


class Tree {
public:
    vi mn;
    int n;

    Tree(int n) : n(n), mn(vi(4 * n, 0)) {
    }

    void setMn(int tree, int lt, int rt, int p, int val) {
        if (rt - lt == 1) {
            mn[tree] = val;
            return;
        }
        int c = (lt + rt) / 2;
        if (p < c) setMn(tree * 2 + 1, lt, c, p, val);
        else setMn(tree * 2 + 2, c, rt, p, val);
        mn[tree] = max(mn[tree * 2 + 1], mn[tree * 2 + 2]);
    }

    int getMn(int tree, int lt, int rt, int l, int r) {
        if (l == lt && r == rt) return mn[tree];
        int ans = 0;
        int c = (lt + rt) / 2;
        if (l < c) ans = getMn(tree * 2 + 1, lt, c, l, min(r, c));
        if (r > c) ans = max(ans, getMn(tree * 2 + 2, c, rt, max(l, c), r));
        return ans;
    }

    void setMn(int p, int val) {
        setMn(0, 0, n, p, val);
    }

    int getMn(int l, int r) {
        return getMn(0, 0, n, l, r);
    }
};

int gcd_(int a, int b) {
    if (a == 0) return b;
    return gcd_(b % a, a);
}

vi getDevs(int x) {
    vi res;
    for (int i = 2; i * i <= x; i++) {
        if (x % i == 0) {
            res.pb(i);
        }
    }
    return res;
}

signed main() {
    int n;
    cin >> n;
    vi vec(n);
    cin >> vec;
    sort(all(vec));
    cout << vec;

}
