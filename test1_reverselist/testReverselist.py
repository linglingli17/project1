import unittest
from reverselist import *

class testReverselist(unittest.TestCase):

    def test_ReverseListPerKNodes_normal(self):

        L = Node(1, Node(2, Node(3, Node(4, Node(5, Node(6, Node(7)))))))
        # h = ReverseList(L)
        r = ReverseListPerKNodes(L, 3)
        p = r
        res = ''
        while p != None:
            res = res + str(p.data)
            p = p.next
        print(res)
        expect="3216547"
        self.assertEquals(expect,res)

    def test_ReverseListPerKNodes_k0(self):

        L = Node(1, Node(2, Node(3, Node(4, Node(5, Node(6, Node(7)))))))
        # h = ReverseList(L)
        r = ReverseListPerKNodes(L, 0)
        p = r
        res = ''
        while p != None:
            res = res + str(p.data)
            p = p.next
        print(res)
        expect="7654321"
        self.assertEquals(expect,res)


if __name__ == '__main__':
    unittest.main()