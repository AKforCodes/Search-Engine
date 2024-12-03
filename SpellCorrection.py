def levenshtein_distance(a, b):
    n, m = len(a), len(b)
    if n > m:
        a, b = b, a
        n, m = m, n

    current_row = range(n + 1)
    for i in range(1, m + 1):
        previous_row, current_row = current_row, [i]+[0]*n
        for j in range(1, n + 1):
            insertions = previous_row[j]+1
            deletions = current_row[j-1]+1
            substitutions = previous_row[j-1]+(a[j-1] != b[i-1])
            current_row[j] = min(insertions, deletions, substitutions)
    return current_row[n]
