export function diffListOrNull<T, U = T>(
  items: T[],
  diffFn: (item: T, index: number) => U | null
): U[] | null {
  const res = items.reduce<U[]>((acc, item, index) => {
    const transformed = diffFn(item, index);
    if (transformed !== null) acc.push(transformed)
    return acc;
  }, []);

  return res.length > 0 ? res : null;
}