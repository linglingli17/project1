import argparse
import sys

class Node(object):
    def __init__(self,data=None,next=None):
        self.data = data
        self.next = next

def ParseArgs():
    #desc = 'usage:'
    parser = argparse.ArgumentParser()

    parser.add_argument('-i',action='store',dest='list',help='The input list')

    parser.add_argument('-k',action='store',dest='knode',help='the number of nodes to reverse')

    args = parser.parse_args()
    return args

def ReverseList(head):
    if head == None:
        return None

    tail = head
    p = head.next
    while p != None:
        r = p.next

        #move the current node to the head of the list
        p.next = head
        head = p
        p = r

    #Finish move all nodes
    tail.next = None
    ####################for test
    #print("in revlist")
    #p = head
    #while p != None:
    #    print(p.data)
    #    p = p.next
    #print ('end')
    ##################################
    return head


def ReverseListPerKNodes(head, K):
    if head == None:
        return None
    if K == 0 or K == 1:
        return head

    #get the sublist with lenth K
    start = head
    p = head
    lasttail = None
    numRuns = 0
    while start is not None:

        count = 1
        p = start
        while p.next != None:
            count += 1
            p = p.next
            if count == K:
                #get a sublist with K node: [start, p]
                break
        #the remainder nodes less than K,leave the list as it is
        if count < K:
            if lasttail is not None:
                lasttail.next = start
            break

        #P is the tail of the current K list,record the start node for the next K nodes
        nextStart = p.next
        #start to reverse the current K nodes
        p.next = None
        rev_start = ReverseList(start)

        numRuns += 1
        if numRuns == 1:
            #change the head pointer after reversing the first K nodes
            head = rev_start
        #linked the reversed K nodes after the last sub list
        if lasttail is not None:
            lasttail.next = rev_start

        lasttail = start
        #Adjust to the current tail node
        start = nextStart
    return head


if __name__=='__main__':

    args = ParseArgs()
    if args != None:
        print('input list= %s, K= %s' % (args.list, args.knode))
    else:
        exit(0)
        # pass

    liststr = args.list
    #liststr= '12345'
    k = int(args.knode)
    #k = 2

    head = tail = None
    for nodeindex in range(len(liststr)):
        node = Node(liststr[nodeindex])
        if head == None:
            #head node
            head = node
            tail = head

        else:
            #link the node to the tail
            tail.next = node
            tail = node

    #L = Node(1,Node(2,Node(3,Node(4,Node(5,Node(6, Node(7)))))))
    #h = ReverseList(L)
    tmp = '->'
    in_list = []
    print ('----------------------')
    p = head
    while(p!= None):
        in_list.append(p.data)
        p = p.next

    print ('the input list is:' + tmp.join(in_list))
    r = ReverseListPerKNodes(head,k)
    p = r

    print ('----------------------')
    out_list = []
    while p != None:
        out_list.append(p.data)
        p = p.next
    print('the output list is:' + tmp.join(out_list))





