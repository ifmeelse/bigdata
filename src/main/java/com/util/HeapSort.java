package com.util;

import java.util.HashMap;

/**
 * @date 2013年8月23日
 * @author lvjq 堆排序
 */
public class HeapSort {

	/**
	 * @date 2013年8月23日
	 * 堆排序
	 */
	public static void heapSort(String[] data,HashMap<String,Integer> dataMap) {
		int arrayLength = data.length;
		// 循环建堆
		for (int i = 0; i < arrayLength - 1; i++) {
			// 建堆
			buildHeap(data, arrayLength - 1 - i,dataMap);
			// 交换堆顶和最后一个元素
			swap(data, 0, arrayLength - 1 - i);
		}
	}

	public static void swap(String[] data, int i, int j) {
		String tmp = data[i];
		data[i] = data[j];
		data[j] = tmp;
	}

	/**
	 * @date 2013年8月23日
	 * 初始化堆
	 */
	public static void buildHeap(String[] data, int lastIndex ,HashMap<String, Integer> dataMap) {
		// 从lastIndex处节点（最后一个节点）的父节点开始
		for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
			// k保存正在判断的节点
			int k = i;
			// 如果当前k节点的子节点存在
			while (k * 2 + 1 <= lastIndex) {
				// k节点的左子节点的索引
				int smallerIndex = 2 * k + 1;
				// 如果smallerIndex小于lastIndex，即smallerIndex+1代表的k节点的右子节点存在
				if (smallerIndex < lastIndex) {
					// 如果右子节点的值较小
					if (dataMap.get(data[smallerIndex])-
							dataMap.get(data[smallerIndex + 1]) > 0) {
						// smallerIndex总是记录较小子节点的索引
						smallerIndex++;
					}
				}
				// 如果k节点的值大于其较小的子节点的值
				if (dataMap.get(data[k]).compareTo(dataMap.get(data[smallerIndex])) > 0) {
					// 交换他们
					swap(data, k, smallerIndex);
					// 将smallerIndex赋予k，开始while循环的下一次循环，重新保证k节点的值小于其左右子节点的值
					k = smallerIndex;
				} else {
					break;
				}
			}
		}
	}

}
