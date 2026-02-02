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
    cout << vec[vec.size() - 2] << " " << vec[vec.size() - 1];

}

// signed main() {
//     int t;
//     cin >> t;
//     while (t--) {
//         int n,m ;cin >> n >> m;
//         string s; cin >> s;
//         vi vec(m);
//         int j = 0;
//         cin >> vec;
//         vi ans(vec);
//
//         int skip = 0;
//         int curPos = 1;
//         int sm = 0;
//         for (int i = 0; i < n; i++) {
//             int tmp = sm;
//             if (s[i] == 'B') {
//                 tmp++;
//                 tmp -= skip;
//             }
//             while (j < m && curPos + tmp >= vec[j]) {
//                 j++;
//                 tmp++;
//             }
//             // cout << "POS: " << curPos << "tmp: " << tmp << " newPos: " << curPos + tmp << "\n";
//             curPos += tmp;
//
//
//             if (s[i] == 'B') {
//                 ans.pb(curPos);
//                 sm++;
//             } else {
//                 curPos++;
//                 if (j < m && curPos == vec[j]) {
//                     j++;
//                     skip++;
//                 } else {
//                     sm++;
//                     ans.pb(curPos);
//                 }
//             }
//
//         }
//         sort(all(ans));
//         cout << ans <<  "\n";
//     }
// }


// signed main() {
// 	int t;
// 	cin >> t;
// 	while (t--) {
// 		int n, q; cin >> n >> q;
// 		string s;
// 		cin >> s;
// 		vector<vi> count(n, vi(26));
// 		vi ans(n);
// 		for (int i = 0; i < n; i++) {
// 			ans[i] = i + 1;
// 		}
// 		vi suffix(26);
// 		for (int i = n - 1; i >= 0; i--) {
// 			int cur = s[i] - 'a';
//
// 		}
// 		for (int i = 0; i < q; i++) {
// 			int act; cin >> act;
// 			if (act == 1) {
// 				int idx;
// 				cin >> idx;
// 				cout << ans[i - 1] << "\n";
// 			} else {
// 				int l, r; cin >> l >> r;
//
// 			}
// 		}
//
// 	}
//
// }
//