import React from 'react'
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native'
import { Pressable, Swipeable } from 'react-native-gesture-handler'

import { styles } from './styles'

type SwipeableItemProps = {
  content: React.ReactNode
  onPress: () => void
  onLongPress: () => void
  onDelete: () => void
}

export function SwipeableItem({ content, onPress, onLongPress, onDelete }: SwipeableItemProps) {
  return (
    <Swipeable
      renderRightActions={() => (
        <TouchableOpacity style={styles.deleteButton} onPress={onDelete}>
          <Text style={styles.deleteText}>Delete</Text>
        </TouchableOpacity>
      )}
    >
      <Pressable style={styles.item} onPress={onPress} onLongPress={onLongPress}>
        {content}
      </Pressable>
    </Swipeable>
  )
}
