import { StyleSheet } from 'react-native';

const focusedColor = "#D4AF37";
const unfocusedColor = '#B0B0B0';

export const styles = StyleSheet.create({
    tabBarStyle: {
      tabBarActiveTintColor: focusedColor,
      tabBarInactiveTintColor: unfocusedColor,
      tabBarStyle: {
        backgroundColor: '#0D0605', // Changed to very dark brown-black
        borderTopWidth: 0,
      },
      tabBarLabelStyle: {
        fontSize: 14,
        marginBottom: 5,
      },
    } as any,
    focusedColor: focusedColor as any,
    unfocusedColor: unfocusedColor as any,
});