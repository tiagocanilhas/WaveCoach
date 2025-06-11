import * as React from 'react'
import { View, TouchableOpacity } from 'react-native'
import DraggableFlatList from 'react-native-draggable-flatlist'
import Ionicons from '@expo/vector-icons/Ionicons'

import { styles } from './styles'

type VerticalReorderableListProps<T> = {
  data: T[]
  onDragEnd: (data: T[]) => void
  keyExtractor: (item: T) => string
  renderItem: (item: T, drag: () => void, isActive: boolean) => React.ReactNode
  onAdd?: () => void
}

export function VerticalReorderableList<T>({
  data,
  onDragEnd,
  keyExtractor,
  renderItem,
  onAdd,
}: VerticalReorderableListProps<T>) {
  return (
    <View style={styles.container}>
      <DraggableFlatList<T>
        data={data}
        onDragEnd={({ data }) => onDragEnd(data)}
        keyExtractor={keyExtractor}
        renderItem={({ item, drag, isActive }) => renderItem(item, drag, isActive)}
        ListFooterComponent={
          onAdd && (
            <View style={styles.addButtonContainer}>
              <TouchableOpacity onPress={onAdd}>
                <Ionicons name="add-circle" size={50} color="black" />
              </TouchableOpacity>
            </View>
          )
        }
      />
    </View>
  )
}
