

class Node(object):
    def __init__(self,data=None,next=None):
        self.data = data
        self.next = next


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
    L = Node(1,Node(2,Node(3,Node(4,Node(5,Node(6, Node(7)))))))
    #h = ReverseList(L)
    r = ReverseListPerKNodes(L,3)
    p = r
    while p != None:
        print(p.data)
        p = p.next





