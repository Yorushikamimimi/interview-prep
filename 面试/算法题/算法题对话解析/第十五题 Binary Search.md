**

题目：Binary Search

示例：

- nums = [-1,0,3,5,9,12], target = 9 → 返回 4
    
- nums = [-1,0,3,5,9,12], target = 2 → 返回 -1
    

## 先给结论

这题属于 Binary Search（二分查找）。  
本质是：利用数组有序这一条件，每次通过中间位置排除一半区间，从而快速定位目标值。

---

## 已知事实

1. 题目给的是 有序数组，这是能用二分的前提。
    
2. 每次取中间位置 mid：
    

- 如果 nums[mid] == target，直接返回
    
- 如果 nums[mid] < target，去右半边找
    
- 如果 nums[mid] > target，去左半边找
    

4. 常见维护的是 闭区间 [left, right]
    
5. 当区间缩到无效还没找到时，返回 -1
    

---

## 合理推测

1. 这题是很多二分题的母题，重点不是代码，而是：
    

- 为什么能排除一半
    
- 区间怎么收缩
    
- 为什么不会漏答案
    

3. 后续很多变形题都基于它：
    

- 找左边界
    
- 找右边界
    
- 旋转数组搜索
    
- 答案二分
    

---

## 需要验证

1. 你是否真的理解：有序性 是二分成立的根本原因
    
2. 你是否能分清：
    

- left = mid + 1
    
- right = mid - 1  
    为什么必须这样写
    

4. 你是否知道循环条件为什么通常写 left <= right
    

---

## 核心术语

- Binary Search：每次根据中间位置，把搜索范围缩小一半的查找方法。
    
- Sorted Array：有序数组，元素有顺序。
    
- mid：当前搜索区间的中间下标。
    
- Search Space：搜索区间，也就是当前还可能存在答案的范围。
    

---

## baseline solution

朴素解：从左到右遍历整个数组，找到就返回下标，否则返回 -1。

- Time Complexity：O(n)
    
- Space Complexity：O(1)
    

问题在于：没有利用“有序”这个条件。

---

## optimal solution

最优解：Binary Search

主流程：

1. 初始化 left = 0, right = n - 1
    
2. 当 left <= right 时：
    

- 计算 mid
    
- 比较 nums[mid] 和 target
    
- 决定去左边还是右边
    

4. 找到就返回下标
    
5. 循环结束还没找到，返回 -1
    

---

## 伪代码骨架

left = 0

right = n - 1

  

while left <= right:

    mid = left + (right - left) // 2

  

    if nums[mid] == target:

        return mid

    elif nums[mid] < target:

        left = mid + 1

    else:

        right = mid - 1

  

return -1

  

---

## 为什么 baseline 慢，optimal 快

- baseline：一个个试，最坏要看完整个数组
    
- optimal：每次直接砍掉一半区间
    

所以：

- 遍历是 线性缩小
    
- 二分是 指数缩小
    

---

## 容易错的点

1. 忘了数组必须有序
    
2. 区间定义混乱
    

- 如果你用闭区间 [left, right]，循环条件一般就是 left <= right
    

4. 边界更新写错
    

- nums[mid] < target 时，必须 left = mid + 1
    
- nums[mid] > target 时，必须 right = mid - 1
    

6. mid 不能一直不动
    

- 否则会死循环
    

8. 找不到时要返回 -1
    

---

## 面试可复述版本

“这题是标准二分查找。因为数组有序，所以我可以通过中间位置和 target 的比较结果，直接排除一半不可能的区间。若中间值小于 target，说明答案只可能在右侧；若大于 target，说明答案只可能在左侧。我维护闭区间 [left, right]，循环条件是 left <= right，每次更新边界为 mid + 1 或 mid - 1。时间复杂度是 O(log n)，空间复杂度是 O(1)。”

---

## 一句话题型总结

这题属于 Binary Search，本质是在有序数组中通过中间值不断缩小搜索区间。

## 一句话核心 insight

这题最关键的突破点是：数组有序，所以比较 mid 后可以安全丢掉一半区间。

## Markdown 权衡表

|   |   |   |   |   |   |
|---|---|---|---|---|---|
|方案|Core Idea|Time Complexity|Space Complexity|适用场景|风险|
|遍历|从头到尾逐个比较|O(n)|O(1)|数组无序、先求可行解|没利用有序性，效率低|
|Binary Search|利用有序性，每次排除一半区间|O(log n)|O(1)|有序数组查找目标值|易写错边界、区间定义和循环条件|

  
**