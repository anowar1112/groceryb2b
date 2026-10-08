create policy "admins read all shops" on public.shops
for select to authenticated using (public.is_admin());