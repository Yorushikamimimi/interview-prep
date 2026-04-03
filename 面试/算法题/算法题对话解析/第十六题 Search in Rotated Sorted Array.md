**

## 先给结论

这题属于 Binary Search（二分查找）变形题。  
本质不是“找旋转点”，而是：每次二分时，至少有一半区间仍然有序，利用这半边的有序性判断 target 在哪边。

---

## 已知事实

1. 原数组本来是升序数组，只是被旋转了一次。
    
2. 旋转后数组整体不完全有序，但只有一个断点。
    
3. 因为只有一个断点，所以每次取 mid 后：
    

- 要么左半边有序
    
- 要么右半边有序
    

5. 找到有序半边后，就能判断 target 是否落在这个有序区间里，从而丢掉另一半。
    

---

## 合理推测

1. 这题最容易想到的 baseline 是线性扫描。
    
2. 真正的优化点在于：数组虽然旋转了，但并没有乱掉，仍保留了二分所需的局部有序性。
    
3. 面试里更推荐直接讲“二分过程中判断哪半边有序”，而不是先找 pivot 再二分，虽然两种都能做。
    

---

## 需要验证

如果题目变成 有重复元素（比如 LeetCode 81），判断哪半边有序会变得不那么直接，解法要稍作调整。  
但这道题的标准版本默认是 无重复元素。

---

# 1. 题型判断

Rotated Sorted Array + Binary Search

一句话理解：  
在旋转有序数组里做二分，每次利用“至少一半有序”来缩小范围。

---

# 2. baseline solution

### 思路

直接遍历整个数组，找到 target 就返回下标，否则返回 -1。

### 复杂度

- Time Complexity：O(n)
    
- Space Complexity：O(1)
    

### 为什么慢

因为没有利用“旋转有序”这个结构信息，只是顺序扫。

---

# 3. optimal solution

### 核心 insight

每次二分后，左右两边至少有一边是有序的。

### 如何判断哪边有序

设中点为 mid：

- 如果  
    nums[left] <= nums[mid]  
    说明 左半边有序
    
- 否则  
    说明 右半边有序
    

---

# 4. 如何缩小范围

### 情况 A：左半边有序

如果：

nums[left] <= target < nums[mid]

  

说明 target 在左边，收缩到左边。  
否则去右边。

### 情况 B：右半边有序

如果：

nums[mid] < target <= nums[right]

  

说明 target 在右边，收缩到右边。  
否则去左边。

---

# 5. 主流程伪代码

left = 0, right = n - 1

  

while left <= right:

    mid = left + (right - left) // 2

  

    if nums[mid] == target:

        return mid

  

    if nums[left] <= nums[mid]:

        # 左半边有序

        if nums[left] <= target < nums[mid]:

            right = mid - 1

        else:

            left = mid + 1

    else:

        # 右半边有序

        if nums[mid] < target <= nums[right]:

            left = mid + 1

        else:

            right = mid - 1

  

return -1

  

---

# 6. 为什么这样做是对的

因为旋转数组只有一个断点。  
所以任意一次二分，不可能左右两边都乱，必然有一边保持递增。  
而有序区间可以像普通二分一样判断 target 是否在范围内，从而安全地排除另一半。

---

# 7. 容易错的点

1. 区间边界不等号写错
    

- 左边有序时：nums[left] <= target < nums[mid]
    
- 右边有序时：nums[mid] < target <= nums[right]
    

3. 把“mid 已经比较过”忘了
    

- 因为前面已经判断过 nums[mid] == target
    
- 所以后面范围判断里不需要再把 mid 包进去
    

5. 把这题写成先找 pivot
    

- 可以做，但更绕
    
- 面试里直接讲“判断哪边有序”更自然
    

7. 混淆普通二分和这题二分
    

- 普通二分依赖整体有序
    
- 这题依赖“局部至少一半有序”
    

---

# 8. 复杂度分析

设 n 为数组长度。

### optimal

- Time Complexity：O(log n)
    

- 因为每次都能排除一半区间
    

- Space Complexity：O(1)
    

- 只用了几个指针变量
    

### baseline

- Time Complexity：O(n)
    
- Space Complexity：O(1)
    

---

# 9. 面试可复述版本

“这题是二分查找变形。虽然数组被旋转后整体不再完全有序，但由于它本质上只是升序数组在某个点断开重组，所以任意一次二分时，左右两边至少有一边是有序的。  
我先判断哪一边有序，再判断 target 是否落在这个有序区间里，从而决定保留哪一半，整体时间复杂度是 O(log n)，空间复杂度是 O(1)。”

---

# 10. 一句话题型总结

- 这题属于 Binary Search 变形题，本质是在旋转有序数组中利用“至少一半有序”继续二分。
    

# 11. 一句话核心 insight

- 最关键的突破点是：每次取 mid 后，左右两边至少有一边有序，可以据此判断 target 应该保留在哪一边。
    

# 12. Markdown 权衡表

|   |   |   |   |   |   |
|---|---|---|---|---|---|
|方案|Core Idea|Time Complexity|Space Complexity|适用场景|风险|
|Linear Scan|从头到尾遍历，找到 target 就返回|O(n)|O(1)|刚理解题意、先写可行解时|没利用数组结构，效率低|
|Binary Search on Rotated Array|每次判断哪一半有序，再判断 target 是否落在该半边|O(log n)|O(1)|标准题解、面试最优解|边界不等号容易写错，容易混淆左右半边判断|

---

## 最后给你一版超短背诵版

这题是旋转有序数组上的二分查找。核心在于虽然整体无序，但每次二分至少有一半有序。判断哪边有序后，再看 target 是否落在这段有序区间里，从而排除另一半，时间复杂度 O(log n)。

下一题你也可以继续按这个训练模式来。

  
  
**